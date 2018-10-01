package com.cogito.erm.security;


import com.cogito.erm.dao.login.UserLogin;
import com.cogito.erm.filter.AuthenticationFilter;
import com.cogito.erm.model.authentication.AuthenticationWithToken;
import com.cogito.erm.service.EmployeeService;
import com.cogito.erm.service.LoginService;
import com.cogito.erm.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


public class DomainUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(DomainUsernamePasswordAuthenticationProvider.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    public DomainUsernamePasswordAuthenticationProvider(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public AuthenticationWithToken authenticate(Authentication authentication)  {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        AuthenticationWithToken authenticationWithToken = new AuthenticationWithToken(username, password);
        UserLogin userLogin = loginService.login(username,password);
        if(userLogin!=null){
            // generate token
            // return token and the roles for the user
            try {
                String newToken = tokenService.generateNewToken(userLogin);
                authenticationWithToken.setAuthenticated(true);
                authenticationWithToken.setRoles(employeeService.getRolesForUser(userLogin));
                authenticationWithToken.setToken(newToken);

            }
            catch (Exception ex){
                log.error("Exception while generating token {}",ex);
                authenticationWithToken.setAuthenticated(false);
                authenticationWithToken.setToken(null);
            }

        }
        return authenticationWithToken;
    }

    private List<GrantedAuthority> getAuthorities(List<String> roles){
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if(!CollectionUtils.isEmpty(roles)){
            for(String role : roles){
                grantedAuthorities.add(new SimpleGrantedAuthority(role));
            }
        }
        return grantedAuthorities;
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
