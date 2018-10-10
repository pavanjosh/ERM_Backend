package com.cogito.erm.dao.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "employeeRolesAndRosters")
public class EmployeeRolesAndRoster {

    @Id
    private String id;

    private Date rosterStartDate;
    private Date rosterEndDate;
    private List<String> role = new ArrayList<>();
    private String employeeId;
    private String name;
    private String location;

    public Date getRosterStartDate() {
        return rosterStartDate;
    }

    public void setRosterStartDate(Date rosterStartDate) {
        this.rosterStartDate = rosterStartDate;
    }

    public Date getRosterEndDate() {
        return rosterEndDate;
    }

    public void setRosterEndDate(Date rosterEndDate) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
