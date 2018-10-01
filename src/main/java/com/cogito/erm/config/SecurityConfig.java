package com.cogito.erm.config;


import com.cogito.erm.filter.AuthenticationFilter;
import com.cogito.erm.security.DomainUsernamePasswordAuthenticationProvider;
import com.cogito.erm.security.TokenAuthenticationProvider;
import com.cogito.erm.service.TokenService;
import com.cogito.erm.util.ERMUtil;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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
import java.io.File;
import java.io.IOException;

/**
 * Created by pavankumarjoshi on 30/05/2017.
 */
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
        return new String[]{ERMUtil.AUTOCONFIG_ENDPOINT, ERMUtil.BEANS_ENDPOINT, ERMUtil.CONFIGPROPS_ENDPOINT,
                ERMUtil.ENV_ENDPOINT, ERMUtil.MAPPINGS_ENDPOINT,
                ERMUtil.METRICS_ENDPOINT, ERMUtil.SHUTDOWN_ENDPOINT};
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
