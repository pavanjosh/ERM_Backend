package com.cogito.erm.config;


import com.cogito.erm.filter.AuthenticationFilter;
import com.cogito.erm.security.DomainUsernamePasswordAuthenticationProvider;
import com.cogito.erm.security.TokenAuthenticationProvider;
import com.cogito.erm.service.TokenService;
import com.cogito.erm.util.ERMUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
@EnableScheduling
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Value("${jwt.header}")
    private String jwtHeader;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                authorizeRequests().
                antMatchers(actuatorEndpoints())
                .authenticated().
                and().
                anonymous().disable().
                exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());
        //AuthenticationFilter authenticationFilter = new AuthenticationFilter(jwtHeader);

        http.addFilterBefore(new AuthenticationFilter(authenticationManager()),
                BasicAuthenticationFilter.class);
    }

    private String[] actuatorEndpoints() {
        String[] pathArr = new String[ERMUtil.NON_AUTHENTICATION_SPRING_ACTUATOR_PATH_LIST.size()];
        pathArr=ERMUtil.NON_AUTHENTICATION_SPRING_ACTUATOR_PATH_LIST.toArray(pathArr);
        return pathArr;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(domainUsernamePasswordAuthenticationProvider()).
                authenticationProvider(tokenAuthenticationProvider());
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/collab/user/register");
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
    @Bean
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider(tokenService());
    }

    @Bean
    public AuthenticationProvider domainUsernamePasswordAuthenticationProvider() {
        return new DomainUsernamePasswordAuthenticationProvider(tokenService());
    }
    @Bean
    public TokenService tokenService() {
        return new TokenService();
    }



}
