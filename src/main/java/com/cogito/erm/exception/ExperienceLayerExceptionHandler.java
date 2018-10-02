package com.cogito.erm.exception;

import com.cogito.erm.util.ERMUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.net.ConnectException;

@ControllerAdvice
public class ExperienceLayerExceptionHandler {

  private static Logger logger = LoggerFactory.getLogger("ExperienceLayerExceptionHandler");

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<Object> handleServiceException(ServiceException se, WebRequest request) throws IOException {

    CommonExceptionModel commonException = new CommonExceptionModel();
    commonException.setErrorCode(se.getErrorCode());
    commonException.setErrorMessage(se.getErrorMessage());
    commonException.setErrorDescription(se.getErrorDescription());
    commonException.setHttpStatus(se.getHttpStatus());
    commonException.setSystemName((se.getSystemName() != null) ? se.getSystemName() : ERMUtil.SYSTEM_NAME);

    MDC.put(ERMUtil.ACTIVITY_STATUS, String.valueOf(se.getHttpStatus()));
    ObjectMapper objectMapper = new ObjectMapper();
    logger.error("PPSR Experience : Error in Controller Advice {},{}", objectMapper.writeValueAsString(commonException), se);
    return new ResponseEntity<>(commonException, HttpStatus.valueOf(se.getHttpStatus()));
  }

  @ExceptionHandler(value = { ConnectException.class, IOException.class })
  public ResponseEntity<Object> handleConnectionServiceException(ConnectException se, WebRequest request) throws IOException {

    CommonExceptionModel commonException = new CommonExceptionModel();
    commonException.setErrorCode(se.getLocalizedMessage());
    commonException.setErrorMessage(se.getMessage());

    commonException.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    commonException.setSystemName(ERMUtil.SYSTEM_NAME);
    MDC.put(ERMUtil.ACTIVITY_STATUS, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    ObjectMapper objectMapper = new ObjectMapper();
    logger.error("PPSR Experience : Error in Controller advice , rest timeout exception {},{}",
      objectMapper.writeValueAsString(commonException), se);
    return new ResponseEntity<>(commonException, HttpStatus.valueOf(commonException.getHttpStatus()));
  }

}
