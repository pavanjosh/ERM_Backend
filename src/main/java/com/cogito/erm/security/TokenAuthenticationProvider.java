package com.cogito.erm.security;

import com.cogito.erm.service.TokenService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

/**
 * Created by pavankumarjoshi on 31/05/2017.
 */
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private TokenService tokenService;

    public TokenAuthenticationProvider(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String token = authentication.getPrincipal().toString();
//        if (token ==  null) {
//            throw new BadCredentialsException("Invalid token");
//        }
//        if (!tokenService.contains(token)) {
//            throw new BadCredentialsException("Invalid token or token expired");
//        }
//        Authentication retrieve = tokenService.retrieve(token);
//        //retrieve.setAuthenticated(true);
//        return retrieve;
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
