package com.cogito.erm.filter;

import com.cogito.erm.util.ERMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(2)
public class LoggingFilter
    extends OncePerRequestFilter {

  private final Logger logger = LoggerFactory.getLogger("LoggingFilter");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (!isAsyncDispatch(request)) {
      HttpServletRequest newRequest;
      if (request instanceof ContentCachingRequestWrapper) {
        newRequest = (ContentCachingRequestWrapper) request;
      }
      else {
        newRequest = new ContentCachingRequestWrapper(request);
      }
      if (logger.isInfoEnabled()) {

        String sourceIpAddr = request.getRemoteAddr();
        String sourcePort = String.valueOf(request.getRemotePort());
        String destinationIpAddr = request.getLocalAddr();
        String destinationPort = String.valueOf(request.getServerPort());
        String protocol = request.getScheme();
        String activity = request.getRequestURI();
        String activityStatus = String.valueOf(response.getStatus());

        String coRelationId = generateCoRelationId();
        logger.debug("Inside logging filter");

        MDC.put(ERMUtil.CORELATION_ID_VALUE, coRelationId);

        MDC.put(ERMUtil.SOURCE_IP, sourceIpAddr);
        MDC.put(ERMUtil.SOURCE_PORT, sourcePort);
        MDC.put(ERMUtil.DESTINATION_IP, destinationIpAddr);
        MDC.put(ERMUtil.DESTINATION_PORT, destinationPort);
        MDC.put(ERMUtil.PROTOCOL, protocol);
        MDC.put(ERMUtil.ACTIVITY, activity);
        MDC.put(ERMUtil.ACTIVITY_STATUS, activityStatus);
      }
    }
    try {
      filterChain.doFilter(request, response);
    }
    finally {
      MDC.clear();
    }
  }

  private String generateCoRelationId() {
    return UUID.randomUUID().toString();
  }

}
