package com.cogito.erm.repository;

import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.model.authentication.LoginResponse;
import com.cogito.erm.util.ERMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class EmployeeRolesAndRosterRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  private static final Logger log = LoggerFactory.getLogger(EmployeeRolesAndRosterRepository.class);

  public String createRolesAndRoster(EmployeeRolesAndRoster employeeRolesAndRoster){

    mongoTemplate.save(employeeRolesAndRoster, ERMUtil.EMPLOYEE_ROLESANDROSTER_COLLECTION);
    return employeeRolesAndRoster.getId();
  }

  public List<String> getRoles(LoginResponse userLogin){
    Query query = new Query();

  try {
    query.addCriteria(Criteria.where("employeeId").is(userLogin.getEmployeeId())
      .andOperator(Criteria.where(ERMUtil.EMPLOYEE_NAME_FILED).is(userLogin.getLoginName())
        .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ROSTER_STARTDATE_FILED).gte(new Date())
          .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ROSTER_ENDDATE_FILED).lte(new Date())))));
    EmployeeRolesAndRoster userRoles = mongoTemplate.findOne(query, EmployeeRolesAndRoster.class);
    if(userRoles!=null){
      return userRoles.getRole();
    }
  }
  catch (Exception ex){
    ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "RolesAndRosterFetchUnsuccessful",
      "Exception while getting roles for the user " +userLogin.getEmployeeId()+":"+userLogin.getLoginName()+":"+
      userLogin.getName()+":"+ex.getLocalizedMessage());
  }

    return null;
  }
}
