package com.cogito.erm.dao.user;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "UserRolesAndRoster")
public class UserRoles {

    private String rosterStartDate;
    private String rosterEndDate;
    private List<String> role = new ArrayList<>();
    private String employeeId;
    private String userName;
    private String location;

    public String getRosterStartDate() {
        return rosterStartDate;
    }

    public void setRosterStartDate(String rosterStartDate) {
        this.rosterStartDate = rosterStartDate;
    }

    public String getRosterEndDate() {
        return rosterEndDate;
    }

    public void setRosterEndDate(String rosterEndDate) {
        this.rosterEndDate = rosterEndDate;
    }


    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
