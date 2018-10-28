package com.cogito.erm.filter;

import com.cogito.erm.exception.CommonExceptionModel;
import com.cogito.erm.exception.ServiceException;
import com.cogito.erm.model.authentication.AuthenticationWithToken;
import com.cogito.erm.security.TokenResponse;
import com.cogito.erm.util.ERMUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
public class AuthenticationFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;
    public static final String AUTHORISATION_TOKEN_KEY = "Authorization";
    public static final String USER_SESSION_KEY = "user";


    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain)
            throws IOException, ServletException {

        LOG.debug("In Authentication Filter");

        if (!isAsyncDispatch(httpRequest)) {

            int BEARER_TOKEN_START = 7;
            Optional<String> userName = Optional.ofNullable(httpRequest.getHeader("X-Auth-Username"));
            Optional<String> password = Optional.ofNullable(httpRequest.getHeader("X-Auth-Password"));
            Optional<String> authorisation = Optional.ofNullable(httpRequest.getHeader(AUTHORISATION_TOKEN_KEY));
            String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);
            UrlPathHelper urlPathHelper = new UrlPathHelper();
            String requestUri = urlPathHelper.getRequestUri(httpRequest);

            try {
                if (requestForUnauthorisedPath(requestUri)) {
                    LOG.debug("This URL requires no Authentication {}",resourcePath);
                }
                else {
                    if (postToAuthenticate(httpRequest, requestUri)) {
                        if (!userName.isPresent() || !password.isPresent()) {
                            throw new InternalAuthenticationServiceException("Employee Name and Password Missing in header");

                        }
                        LOG.debug("In Authentication Filter username {}, password {} ", userName.get());
                        processUserNameAndPasswordAuthentication(httpResponse, userName.get(), password.get());
                        return;
                    }
                    else{

                    }
                    if (authorisation.isPresent()) {
                        //logger.debug("Trying to authenticate user by X-Auth-Token method. Token: {}", token.get());
                        LOG.debug("In Authentication Filter token {} ", authorisation.get());

                        String authorisationHeaderValue = authorisation.get();
                        String authToken = authorisationHeaderValue.substring(BEARER_TOKEN_START);
                        if(StringUtils.isEmpty(authToken)){
                            throw new BadCredentialsException("Please login and provide the authorisation token in request");
                        }
                        else {
                            processTokenAuthentication(httpResponse,authToken);
                        }
                    } else {
                        throw new BadCredentialsException("Please login and provide the authorisation token in request");

                    }
                }

                chain.doFilter(httpRequest, httpResponse);
            } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
                SecurityContextHolder.clearContext();
                LOG.error("Internal authentication service exception {}", internalAuthenticationServiceException);
                sendError(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(),
                 internalAuthenticationServiceException.getMessage());
            } catch (BadCredentialsException badCredentialsException) {
                SecurityContextHolder.clearContext();
                LOG.error("Bad credentials exception {}", badCredentialsException);
                sendError(httpResponse, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name(),
                  badCredentialsException.getMessage());
            } catch (AuthenticationException authenticationException) {
                SecurityContextHolder.clearContext();
                LOG.error("Authentication exception {}", authenticationException);
                sendError(httpResponse, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name(),
                  authenticationException.getMessage());
            } catch (ServiceException se) {
                SecurityContextHolder.clearContext();
                LOG.error("Authentication exception {}", se);
                sendError(httpResponse, se.getHttpStatus(),se.getErrorCode(),
                  se.getErrorMessage());
            }catch (Exception ex) {
                SecurityContextHolder.clearContext();
                LOG.error("Authentication exception {}", ex);
                sendError(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(),
                  ex.getMessage());
            }
            finally {
                MDC.clear();
            }
        }
    }

    private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
        return ERMUtil.AUTHENTICATE_URL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
    }

    private boolean requestForUnauthorisedPath(String resourcePath){
         if(ERMUtil.NON_AUTHENTICATION_APPLICATION_PATH_LIST.contains(resourcePath)
           ||ERMUtil.NON_AUTHENTICATION_SPRING_ACTUATOR_PATH_LIST.contains(resourcePath))
         {
             return true;
         }
        return false;
    }


    private void processTokenAuthentication(HttpServletResponse httpResponse,String token) throws IOException{
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
        Authentication authenticate = authenticationManager.authenticate(requestAuthentication);
        if(authenticate==null ){
            throw new InternalAuthenticationServiceException("Unable to authenticate user");

        }
        else if(!authenticate.isAuthenticated()){
            throw new BadCredentialsException("Not a valid user");

        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);
    }
    private void processUserNameAndPasswordAuthentication(HttpServletResponse httpResponse, String userName, String password)
            throws IOException{

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userName, password);
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if(authenticate==null ){
            throw new InternalAuthenticationServiceException("Unable to authenticate user");

        }
        else if(!authenticate.isAuthenticated()){
            throw new BadCredentialsException("Not a valid user");

        }
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        TokenResponse tokenResponse= new TokenResponse();

        if(authenticate instanceof AuthenticationWithToken){
            tokenResponse.setRoles(((AuthenticationWithToken)authenticate).getRoles());
        }
        tokenResponse.setToken(authenticate.getDetails().toString());
        String tokenJsonResponse = new ObjectMapper().writeValueAsString(tokenResponse);
        httpResponse.addHeader("Content-Type", "application/json");
        httpResponse.getWriter().print(tokenJsonResponse);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }

    private void sendError(HttpServletResponse httpServletResponse, int status, String errorCode, String errorMessage)
      throws JsonProcessingException, IOException {
        logger.error(errorMessage);
        httpServletResponse.setStatus(status);
        SecurityContextHolder.clearContext();
        CommonExceptionModel commonException = new CommonExceptionModel();
        commonException.setErrorCode(errorCode);
        commonException.setErrorMessage(errorMessage);
        commonException.setHttpStatus(status);
        commonException.setSystemName(ERMUtil.SYSTEM_NAME);

        MDC.clear();
        ObjectMapper objectMapper = new ObjectMapper();
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(commonException));

    }
}
