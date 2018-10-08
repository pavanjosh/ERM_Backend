package com.cogito.erm.dao.login;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection="employeeLoginDetails")
public class EmployeeLogin
  implements Serializable{

    private String name;

    @Id
    private String securityLicenseNumber;

    private String password;
    private String createdDate;
    private boolean status;
    private String lastLogin;
    private String employeeId;

    public EmployeeLogin(){}

    public String getSecurityLicenseNumber() {
        return securityLicenseNumber;
    }

    public void setSecurityLicenseNumber(String securityLicenseNumber) {
        this.securityLicenseNumber = securityLicenseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
