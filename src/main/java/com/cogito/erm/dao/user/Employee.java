package com.cogito.erm.dao.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection="employeeDetails")
public class Employee {

  @Id
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  private String employeeId;
  private String name;
  private String nswSecurity;
  private String securityExpiry;
  private String securityClass;
  private String msicNo;
  private String msicExpiry;
  private String firstAidExpiry;
  private String paNswInd;
  private String spotlessInd;
  private String phoneNumber;
  private String gender;
  private String emailId;
  private String uniformSizes;
  private String rsaNumber ;
  private String rsaExpiry;
  private String trafficControl;
  private String trafficControlExpiry;
  private String pfso;
  private boolean active=false;
  private String loginName;

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNswSecurity() {
    return nswSecurity;
  }

  public void setNswSecurity(String nswSecurity) {
    this.nswSecurity = nswSecurity;
  }

  public String getSecurityExpiry() {
    return securityExpiry;
  }

  public void setSecurityExpiry(String securityExpiry) {
    this.securityExpiry = securityExpiry;
  }

  public String getSecurityClass() {
    return securityClass;
  }

  public void setSecurityClass(String securityClass) {
    this.securityClass = securityClass;
  }

  public String getMsicNo() {
    return msicNo;
  }

  public void setMsicNo(String msicNo) {
    this.msicNo = msicNo;
  }

  public String getMsicExpiry() {
    return msicExpiry;
  }

  public void setMsicExpiry(String msicExpiry) {
    this.msicExpiry = msicExpiry;
  }

  public String getFirstAidExpiry() {
    return firstAidExpiry;
  }

  public void setFirstAidExpiry(String firstAidExpiry) {
    this.firstAidExpiry = firstAidExpiry;
  }

  public String getPaNswInd() {
    return paNswInd;
  }

  public void setPaNswInd(String paNswInd) {
    this.paNswInd = paNswInd;
  }

  public String getSpotlessInd() {
    return spotlessInd;
  }

  public void setSpotlessInd(String spotlessInd) {
    this.spotlessInd = spotlessInd;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }

  public String getUniformSizes() {
    return uniformSizes;
  }

  public void setUniformSizes(String uniformSizes) {
    this.uniformSizes = uniformSizes;
  }

  public String getRsaNumber() {
    return rsaNumber;
  }

  public void setRsaNumber(String rsaNumber) {
    this.rsaNumber = rsaNumber;
  }

  public String getRsaExpiry() {
    return rsaExpiry;
  }

  public void setRsaExpiry(String rsaExpiry) {
    this.rsaExpiry = rsaExpiry;
  }

  public String getTrafficControl() {
    return trafficControl;
  }

  public void setTrafficControl(String trafficControl) {
    this.trafficControl = trafficControl;
  }

  public String getTrafficControlExpiry() {
    return trafficControlExpiry;
  }

  public void setTrafficControlExpiry(String trafficControlExpiry) {
    this.trafficControlExpiry = trafficControlExpiry;
  }

  public String getPfso() {
    return pfso;
  }

  public void setPfso(String pfso) {
    this.pfso = pfso;
  }
}
