package com.cogito.erm.service;

import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.model.authentication.LoginResponse;
import com.cogito.erm.repository.EmployeeRolesAndRosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeRolesAndRosterService {

  @Autowired
  private EmployeeRolesAndRosterRepository employeeRolesAndRosterRepository;

  public String createRolesAndRoster(EmployeeRolesAndRoster employeeRolesAndRoster){
    String id = employeeRolesAndRosterRepository.createRolesAndRoster(employeeRolesAndRoster);
    return id;
  }

  public List<String> getRolesForEmployee(LoginResponse userLogin){
    return employeeRolesAndRosterRepository.getRoles(userLogin);
  }

}
