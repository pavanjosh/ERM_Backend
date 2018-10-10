package com.cogito.erm.controller;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

  @Autowired
  private LoginService loginService;

  @RequestMapping(value = "/login/register",method = RequestMethod.POST)
  public ResponseEntity<String> createNewLoginCredentials(@RequestBody EmployeeLogin employeeLogin){
    return new ResponseEntity<String>(loginService.createLoginCredentials(employeeLogin), HttpStatus.OK);
  }
}
