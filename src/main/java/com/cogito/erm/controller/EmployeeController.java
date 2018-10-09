package com.cogito.erm.controller;

import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {


  @Autowired
  private EmployeeService employeeService;

  @RequestMapping(value = "/employees",method = RequestMethod.GET)
  public ResponseEntity<List<Employee>> getEmployees(){
      return new ResponseEntity<List<Employee>>(employeeService.getEmployees(), HttpStatus.OK);
  }

  @RequestMapping(value = "/employee/{searchTerm}/{searchValue}",method = RequestMethod.GET)
  public ResponseEntity<List<Employee>> getEmployee(@PathVariable String searchTerm,
    @PathVariable String searchValue){
    return new ResponseEntity<List<Employee>>(employeeService.searchEmployee(searchTerm,searchValue), HttpStatus.OK);
  }

  @RequestMapping(value = "/employee",method = RequestMethod.PUT)
  public ResponseEntity<String> updateEmployee(@RequestBody Employee updateEmployee){
    return new ResponseEntity<String>(employeeService.updateEmployee(updateEmployee), HttpStatus.OK);
  }

  @RequestMapping(value = "/employee",method = RequestMethod.POST)
  public ResponseEntity<String> createEmployee(@RequestBody Employee createEmployee){
    return new ResponseEntity<String>(employeeService.createEmployee(createEmployee), HttpStatus.OK);
  }

  @RequestMapping(value = "/employee/{id}",method = RequestMethod.DELETE)
  public ResponseEntity<Boolean> deleteEmployee(@PathVariable String id){
    return new ResponseEntity<Boolean>(employeeService.deleteEmployee(id), HttpStatus.OK);
  }
}
