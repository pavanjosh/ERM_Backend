package com.cogito.erm.controller;

import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.service.EmployeeRolesAndRosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RolesAndRosterController {

  @Autowired
  private EmployeeRolesAndRosterService employeeRolesAndRosterService;

  @RequestMapping(value = "/rolesandroster",method = RequestMethod.POST)
  public ResponseEntity<String> createRolesAndRoster(@RequestBody EmployeeRolesAndRoster employeeRolesAndRoster){
    return new ResponseEntity<String>(employeeRolesAndRosterService.createRolesAndRoster(employeeRolesAndRoster), HttpStatus.OK);
  }
}
