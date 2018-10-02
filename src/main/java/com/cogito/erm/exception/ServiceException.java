package com.cogito.erm.exception;

public class ServiceException
    extends RuntimeException {

  private static final long serialVersionUID = 7791276809900762471L;

  private int httpStatus;

  private String errorCode;

  private String errorMessage;

  private String errorDescription;

  private String systemName;


  public ServiceException() {
  };

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

  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ServiceException [httpStatus=").append(httpStatus).append(", errorCode=").append(errorCode).append(", errorMessage=")
      .append(errorMessage).append(", systemName=").append(systemName).append(", errorDescription=").append(errorDescription)
      .append("]");
    return sb.toString();

  }

}
