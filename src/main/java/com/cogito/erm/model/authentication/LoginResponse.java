package com.cogito.erm.model.authentication;

import java.io.Serializable;

public class LoginResponse implements Serializable{

  private String name;
  private String loginName;
  private String employeeId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }
}
