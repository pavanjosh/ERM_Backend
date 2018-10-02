package com.cogito.erm.model.authentication;

import java.io.Serializable;

public class JwtTokenValidationStatus
  implements Serializable{

  private String statusMessage;
  private boolean validationStatus = false;

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public boolean isValidationStatus() {
    return validationStatus;
  }

  public void setValidationStatus(boolean validationStatus) {
    this.validationStatus = validationStatus;
  }
}
