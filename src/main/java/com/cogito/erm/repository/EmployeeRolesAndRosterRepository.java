package com.cogito.erm.repository;

import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.util.ERMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRolesAndRosterRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  private static final Logger log = LoggerFactory.getLogger(EmployeeRolesAndRosterRepository.class);

  public String createRolesAndRoster(EmployeeRolesAndRoster employeeRolesAndRoster){

    mongoTemplate.save(employeeRolesAndRoster, ERMUtil.EMPLOYEE_ROLESANDROSTER_COLLECTION);
    return employeeRolesAndRoster.getId();
  }
}
