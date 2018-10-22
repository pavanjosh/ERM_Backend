package com.cogito.erm.controller;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

  @Autowired
  private LoginService loginService;

  /**
   * Creates a new login credentials
   * @param employeeLogin
   * @return
   */
  @RequestMapping(value = "/login/register",method = RequestMethod.POST)
  public ResponseEntity<String> createNewLoginCredentials(@RequestBody EmployeeLogin employeeLogin){
    return new ResponseEntity<String>(loginService.createLoginCredentials(employeeLogin), HttpStatus.OK);
  }

  /**
   * updates login credentials
   * @param employeeLogin
   * @return
   */
  @RequestMapping(value = "/login/update",method = RequestMethod.POST)
  public ResponseEntity<String> updateNewLoginCredentials(@RequestBody EmployeeLogin employeeLogin){
    return new ResponseEntity<String>(loginService.updateLoginCredentials(employeeLogin), HttpStatus.OK);
  }

  @RequestMapping(value = "/login/details/{loginName}",method = RequestMethod.GET)
  public ResponseEntity<EmployeeLogin> getNewLoginCredentials(@RequestHeader(name = "X-Employee-Id",required = true)
                                                  String employeeId,@PathVariable(name = "loginName") String loginName ){
    return new ResponseEntity<EmployeeLogin>(loginService.getLoginCredentials(employeeId,loginName), HttpStatus.OK);

  }

}
