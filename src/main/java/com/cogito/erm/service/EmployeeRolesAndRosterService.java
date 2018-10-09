package com.cogito.erm.service;

import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.repository.EmployeeRolesAndRosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeRolesAndRosterService {

  @Autowired
  private EmployeeRolesAndRosterRepository employeeRolesAndRosterRepository;

  public String createRolesAndRoster(EmployeeRolesAndRoster employeeRolesAndRoster){
    String id = employeeRolesAndRosterRepository.createRolesAndRoster(employeeRolesAndRoster);
    return id;
  }
}
