package com.cogito.erm.repository;

import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRolesAndRosterRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  public String createRolesAndRoster(EmployeeRolesAndRoster employeeRolesAndRoster){

    return null;
  }
}
