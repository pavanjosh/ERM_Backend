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


  /**
   * DUMMY METHODS DELETE THEM
   * Returns the list of ALL employees
   * @return
   */
  @RequestMapping(value = "/registerEmployee",method = RequestMethod.POST)
  public ResponseEntity<Employee> registerEmployeeEmployees(@RequestBody Employee createEmployee){
    return new ResponseEntity<Employee>(employeeService.createEmployee(createEmployee), HttpStatus.OK);
  }

  /**
   * Returns the list of ALL employees
   * @return
   */
  @RequestMapping(value = "/employees",method = RequestMethod.GET)
  public ResponseEntity<List<Employee>> getEmployees(){
      return new ResponseEntity<List<Employee>>(employeeService.getEmployees(), HttpStatus.OK);
  }

  /**
   * Searches the list of employees with the given search term. The search term should be anything
   * of the employee properties
   * @param searchTerm
   * @param searchValue
   * @return
   */
  @RequestMapping(value = "/employee/{searchTerm}/{searchValue}",method = RequestMethod.GET)
  public ResponseEntity<List<Employee>> getEmployee(@PathVariable String searchTerm,
    @PathVariable String searchValue){
    return new ResponseEntity<List<Employee>>(employeeService.searchEmployee(searchTerm,searchValue), HttpStatus.OK);
  }

  /**
   * Updates an employee with the new details. Employee ID in the request object is compulsory
   * @param updateEmployee
   * @return
   */
  @RequestMapping(value = "/employee",method = RequestMethod.PUT)
  public ResponseEntity<Employee> updateEmployee(@RequestBody Employee updateEmployee){
    return new ResponseEntity<Employee>(employeeService.updateEmployee(updateEmployee), HttpStatus.OK);
  }

  /**
   * Creates a new employee
   * @param createEmployee
   * @return
   */
  @RequestMapping(value = "/employee",method = RequestMethod.POST)
  public ResponseEntity<Employee> createEmployee(@RequestBody Employee createEmployee){
    return new ResponseEntity<Employee>(employeeService.createEmployee(createEmployee), HttpStatus.OK);
  }

  /**
   * deletes a given employee
   * @param id
   * @return
   */
  @RequestMapping(value = "/employee/{id}",method = RequestMethod.DELETE)
  public ResponseEntity<Boolean> deleteEmployee(@PathVariable String id){
    return new ResponseEntity<Boolean>(employeeService.deleteEmployee(id), HttpStatus.OK);
  }

  /**
   * returns a list of employee who have login names. This is required to populate the dropdown
   * to create a roster for an employee
   * @return
   */
  @RequestMapping(value = "/employees/rosters/",method = RequestMethod.GET)
  public ResponseEntity<List<Employee>> getEmployeesWithLoginNamesEmployee(){
    return new ResponseEntity<List<Employee>>(employeeService.getEmployeesWithLoginNames(), HttpStatus.OK);
  }
}
