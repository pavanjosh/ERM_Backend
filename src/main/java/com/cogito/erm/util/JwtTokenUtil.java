package com.cogito.erm.util;

import com.cogito.erm.model.authentication.JwtTokenValidationStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtTokenUtil
  implements Serializable {



    private Clock clock = DefaultClock.INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.secret}")
    private String secret;

    @Value("#{'${jwt.mandatory.fields}'.split(',')}")
    private List<String> jwtMandatoryFields;

    public List<String> getRolesFromToken(String token) throws UnsupportedEncodingException{
      return getClaimFromToken(token, (claims) -> {
        return (List<String>) claims.get("roles");
      });
    }
    public String getClientIdFromToken(String token)  throws UnsupportedEncodingException{
      return getClaimFromToken(token, (claims) -> {
        Object clientId = claims.get("clientId");
        return (clientId!=null)? (String)claims.get("clientId"):"-1";
      });
    }

    public String getSubjectFromToken(String token) throws UnsupportedEncodingException{
      return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims,T> function) throws UnsupportedEncodingException{
        final Claims claims = Jwts.parser()
          .setSigningKey(getSecretBytes())
          .parseClaimsJws(token)
          .getBody();

      return function.apply(claims);
    }

    public JwtTokenValidationStatus validateToken(String token){
      JwtTokenValidationStatus jwtTokenValidationStatus = new JwtTokenValidationStatus();
      try {
        logger.debug("Inside JWT util validate token auth filter");
        Jwts.parser()
          .setSigningKey(getSecretBytes())
          .parseClaimsJws(token);

        if(!validateMandatoryFieldsInToken(token)){
          logger.error("Token is NOT valid, Mandatory claims not in token");
          jwtTokenValidationStatus.setStatusMessage("Token is NOT valid, Mandatory claims not in token");
          jwtTokenValidationStatus.setValidationStatus(false);
          return jwtTokenValidationStatus;
        }
        logger.info("Token is valid");
        jwtTokenValidationStatus.setStatusMessage("Token is Valid");
        jwtTokenValidationStatus.setValidationStatus(true);
      }
      catch (IllegalArgumentException e) {
        logger.error("JWT String argument cannot be null or empty {}", e);
        jwtTokenValidationStatus.setStatusMessage("JWT String argument cannot be null or empty");
        jwtTokenValidationStatus.setValidationStatus(false);
      } catch (ExpiredJwtException e) {
        logger.error("Token has expired {}",e);
        jwtTokenValidationStatus.setStatusMessage("JWT Token has expired");
        jwtTokenValidationStatus.setValidationStatus(false);
      }
      catch (SignatureException ex){
        logger.error("Signature does not match {} ",ex);
        jwtTokenValidationStatus.setStatusMessage("Signature does not match");
        jwtTokenValidationStatus.setValidationStatus(false);
      }
      catch (PrematureJwtException ex){
        logger.error("Token is used before designated time {} ",ex);
        jwtTokenValidationStatus.setStatusMessage("Token is used before designated time");
        jwtTokenValidationStatus.setValidationStatus(false);
        return jwtTokenValidationStatus;
      }
      catch (MalformedJwtException ex){
        logger.error("JWT token string is malformed {} ",ex);
        jwtTokenValidationStatus.setStatusMessage("JWT token string is malformed");
        jwtTokenValidationStatus.setValidationStatus(false);
        return jwtTokenValidationStatus;
      }
      catch(Exception ex){
        logger.error("Exception while validating the token {} ",ex);
        jwtTokenValidationStatus.setStatusMessage("Exception while validating the token");
        jwtTokenValidationStatus.setValidationStatus(false);
      }
      return jwtTokenValidationStatus;
    }
    public byte[] getSecretBytes() throws UnsupportedEncodingException{
      //StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
      //textEncryptor.setPassword("ERMCogitoTechnologySolutions");
      //String decrypt = textEncryptor.decrypt(secret);
      String decrypt = secret;
      return Base64.getUrlDecoder().decode(decrypt.getBytes("UTF-8"));
    }
  private boolean validateMandatoryFieldsInToken(String token) throws InvalidParameterException, UnsupportedEncodingException {
      for(String filed: jwtMandatoryFields){
        Object claim = getClaimFromToken(token, (claims) -> {
          return claims.get(filed);
        });
        if(claim == null)return false;
      }
    logger.debug("All mandatory fields in the token present");
      return true;
  }
}
