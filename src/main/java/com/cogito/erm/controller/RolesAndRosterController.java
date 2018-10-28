package com.cogito.erm.controller;

import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.service.EmployeeRolesAndRosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RolesAndRosterController {

  @Autowired
  private EmployeeRolesAndRosterService employeeRolesAndRosterService;

  @RequestMapping(value = "/rolesandroster",method = RequestMethod.POST)
  public ResponseEntity<String> createRolesAndRoster(@RequestBody EmployeeRolesAndRoster employeeRolesAndRoster){
    return new ResponseEntity<String>(employeeRolesAndRosterService.createRolesAndRoster(employeeRolesAndRoster), HttpStatus.OK);
  }

  @RequestMapping(value = "/rolesandroster",method = RequestMethod.PUT)
  public ResponseEntity<String> updateRolesAndRoster(@RequestBody EmployeeRolesAndRoster employeeRolesAndRoster){
    return new ResponseEntity<String>(employeeRolesAndRosterService.updateRolesAndRoster(employeeRolesAndRoster), HttpStatus.OK);
  }


  @RequestMapping(value = "/rolesandroster",method = RequestMethod.GET)
  public ResponseEntity<List<EmployeeRolesAndRoster>> getAllRolesAndRoster(){
    return new ResponseEntity<List<EmployeeRolesAndRoster>>(employeeRolesAndRosterService.getAllRosterDetails(), HttpStatus.OK);
  }

  @RequestMapping(value = "/rolesandroster/{id}",method = RequestMethod.DELETE)
  public ResponseEntity<String> deleteRolesAndRoster(@PathVariable(name = "id") String id){
    return new ResponseEntity<String>(employeeRolesAndRosterService.deleteRosterDetails(id), HttpStatus.OK);
  }
}
