package com.cogito.erm.dao.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="EmployeeDetails")
public class Employee {

  @Id
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String securityLicenseNumber;

  public String getSecurityLicenseNumber() {
    return securityLicenseNumber;
  }

  public void setSecurityLicenseNumber(String securityLicenseNumber) {
    this.securityLicenseNumber = securityLicenseNumber;
  }

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
