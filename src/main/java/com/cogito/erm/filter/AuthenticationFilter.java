package com.cogito.erm.filter;


import com.cogito.erm.model.authentication.AuthenticationWithToken;
import com.cogito.erm.security.TokenResponse;
import com.cogito.erm.util.ERMUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
    public static final String AUTHORISATION_TOKEN_KEY = "Authorisation";
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
            try {
                if (requestForUnauthorisedPath(resourcePath)) {

                LOG.debug("This URL requires no Authentication {}",resourcePath);
                }
                else {
                    if (postToAuthenticate(httpRequest, resourcePath)) {
                        if (!userName.isPresent() || !password.isPresent()) {
                            throw new InternalAuthenticationServiceException("User Name and Password Missing in header");
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
                            processTokenAuthentication(authToken);
                        }
                    } else {
                        throw new BadCredentialsException("Please login and provide the authorisation token in request");
                    }
                }
                addSessionContextToLogging(httpRequest);
                chain.doFilter(httpRequest, httpResponse);
            } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
                SecurityContextHolder.clearContext();
                LOG.error("Internal authentication service exception {}", internalAuthenticationServiceException);
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (BadCredentialsException authenticationException) {
                SecurityContextHolder.clearContext();
                LOG.error("Bad credentials exception {}", authenticationException);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
            } catch (AuthenticationException authenticationException) {
                SecurityContextHolder.clearContext();
                LOG.error("Authentication exception {}", authenticationException);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
            } finally {
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

    private void addSessionContextToLogging(HttpServletRequest httpRequest){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication != null) {

            String token = (String) authentication.getPrincipal().toString();
            String userValue = (String) authentication.getDetails().toString();
            MDC.put(AUTHORISATION_TOKEN_KEY, token);
            MDC.put(USER_SESSION_KEY, userValue);
        }

    }
    private void processTokenAuthentication(String token){
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
}
