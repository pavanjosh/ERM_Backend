package com.cogito.erm.service;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.model.authentication.JwtTokenValidationStatus;
import com.cogito.erm.util.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class TokenService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String generateNewToken(EmployeeLogin userLogin) throws UnsupportedEncodingException{

        return generateJwt(userLogin);
    }
    private String generateJwt(EmployeeLogin userLogin)throws UnsupportedEncodingException {
        Instant now = Instant.now();
        Map<String,Object> claims = new HashMap<>();
        claims.put("clientId",userLogin.getEmployeeId());
        claims.put("timezone", "Pacific/Auckland");
        claims.put("locale", "en_NZ");
        claims.put("employeeName",userLogin.getName());

        int tokenDuration = 24;
        return Jwts.builder()
          .setClaims(claims)
          .setSubject(userLogin.getName())
          .setExpiration(Date.from(now.plus(tokenDuration, ChronoUnit.HOURS)))
          .setIssuedAt(Date.from(now))
          .setNotBefore(Date.from(now))
          .setIssuer("ERMCogito")
          .signWith(SignatureAlgorithm.HS256, jwtTokenUtil.getSecretBytes()).compact();
    }

    public boolean  validateToken(String token){
        JwtTokenValidationStatus jwtTokenValidationStatus = jwtTokenUtil.validateToken(token);
        if (jwtTokenValidationStatus!=null && jwtTokenValidationStatus.isValidationStatus()){
            logger.debug("Token validated successfully");
            try{
                String clientIdFromToken = jwtTokenUtil.getClientIdFromToken(token);
                if(!clientIdFromToken.equalsIgnoreCase("-1")) {
                    MDC.put("clientId", jwtTokenUtil.getClientIdFromToken(token));
                }
                else{
                    logger.error("Invalid client id in the token {}",clientIdFromToken);
                    throw new BadCredentialsException(jwtTokenValidationStatus.getStatusMessage());
                }
                MDC.put("userName", String.valueOf(jwtTokenUtil.getSubjectFromToken(token)));
                MDC.put("roles", String.valueOf(jwtTokenUtil.getRolesFromToken(token)));
                return true;
            }
            catch(UnsupportedEncodingException ex){
                logger.error("Unsupported encoding exception while getting client id from token {}",ex);
            }

        }
        else{
            logger.error("error while validating the token {}",jwtTokenValidationStatus.getStatusMessage());
            throw new BadCredentialsException(jwtTokenValidationStatus.getStatusMessage());
        }
        return false;
    }


}
