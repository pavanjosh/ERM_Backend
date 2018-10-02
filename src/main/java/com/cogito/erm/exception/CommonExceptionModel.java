package com.cogito.erm.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonExceptionModel
    implements Serializable {

  private static final long serialVersionUID = -7808425905215338899L;

  private int httpStatus;

  private String errorCode;

  private String errorMessage;

  private String errorDescription;

  private String systemName;



  public CommonExceptionModel() {
  }

  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("CommonExceptionModel [systemName=").append(systemName).append(", httpStatus=").append(httpStatus).append(", errorCode=")
      .append(errorCode).append(", errorMessage=").append(errorMessage).append(", errorDescription=").append(errorDescription)
      .append("]");
    return sb.toString();
  }

}
