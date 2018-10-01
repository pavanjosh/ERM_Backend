package com.cogito.erm.service;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.*;

import com.cogito.erm.dao.login.UserLogin;
import com.cogito.erm.dao.user.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class TokenService {

    //private static final Cache restApiAuthTokenCache = CacheManager.getInstance().getCache("restApiAuthTokenCache");
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secret;
    public String generateNewToken(UserLogin userLogin) throws UnsupportedEncodingException{

        return generateJwt(userLogin);
    }
    private String generateJwt(UserLogin userLogin)throws UnsupportedEncodingException {
        Instant now = Instant.now();
        Map<String,Object> claims = new HashMap<>();
        claims.put("clientId",userLogin.getEmployeeId());
        claims.put("timezone", "Pacific/Auckland");
        claims.put("locale", "en_NZ");
        int tokenDuration = 24;
        return Jwts.builder()
                .setSubject(userLogin.getUserName())
                .setExpiration(Date.from(now.plus(tokenDuration, ChronoUnit.HOURS)))
                .setIssuedAt(Date.from(now))
               .setClaims(claims)
                .setNotBefore(Date.from(now))
                .setIssuer("ERM")
                .signWith(SignatureAlgorithm.HS256,getSecretBytes()).compact();

    }

    private byte[] getSecretBytes() throws UnsupportedEncodingException {
//        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
//        textEncryptor.setPassword("ERMSecret");
//        String decrypt = textEncryptor.decrypt(secret);
        return Base64.getUrlDecoder().decode(secret.getBytes("UTF-8"));
    }

}
