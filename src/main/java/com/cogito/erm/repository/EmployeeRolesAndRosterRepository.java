package com.cogito.erm.repository;

import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.model.authentication.LoginResponse;
import com.cogito.erm.util.ERMUtil;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class EmployeeRolesAndRosterRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  private static final Logger log = LoggerFactory.getLogger(EmployeeRolesAndRosterRepository.class);

  public String createRolesAndRoster(EmployeeRolesAndRoster employeeRolesAndRoster){

    if(!isValidEmployeeRolesAndRosterRequest(employeeRolesAndRoster))
    {
      log.error("please provide complete details like login name, name, employee id and start date to create a role");
      ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyRolesAndRoster",
              "please provide complete details like login name, name , employee id and start date to create a role");

    }


    // check if the employee is present before saving the roster details for the employee
    if(employeeRolesAndRoster!=null) {
      String employeeId =  employeeRolesAndRoster.getEmployeeId();
      if(!StringUtils.isEmpty(employeeId)){
        Employee employee = mongoTemplate.findOne(
                new Query().addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeId)
                        .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ACTIVE_FILED).is(true)
                        .andOperator(Criteria.where("loginName").is(employeeRolesAndRoster.getLoginName())
                        .andOperator(Criteria.where("name").is(employeeRolesAndRoster.getName())))))

                ,Employee.class,
                ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        if(employee!=null){
          mongoTemplate.save(employeeRolesAndRoster, ERMUtil.EMPLOYEE_ROLESANDROSTER_COLLECTION);
          log.info("Roster created successfully with id {}",employeeRolesAndRoster.getId());
          return employeeRolesAndRoster.getId();
        }
        else{
          log.error("Invalid employee Id for the roster object to save");
          ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyEmployeeForRolesAndRoster",
                  "Invalid employee Id for the roster object to save");
        }
      }
      else{
        log.error("Invalid employee Id for the roster object to save");
        ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyRolesAndRoster",
                "Invalid employee Id for the roster object to save");
      }

    }
    else{
      log.error("Empty roster object to save");
      ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyRolesAndRoster",
              "Invalid roster object to save");
    }

    return null;
  }

  public String updateRolesAndRoster(EmployeeRolesAndRoster employeeRolesAndRoster){

    // Check for basic validations for employee id, login name and name
    if(!isValidEmployeeRolesAndRosterRequest(employeeRolesAndRoster))
    {
      log.error("please provide complete details like login name, name, employee id and start date to create a role");
      ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyRolesAndRoster",
              "please provide complete details like login name, name , employee id and start date to create a role");

    }

    // check if the employee is present before saving the roster details for the employee
    if(employeeRolesAndRoster!=null) {
      String employeeId =  employeeRolesAndRoster.getEmployeeId();
      if(!StringUtils.isEmpty(employeeId)){
        Employee employee = mongoTemplate.findOne(
                new Query().addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeId)
                        .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ACTIVE_FILED).is(true)
                                .andOperator(Criteria.where("loginName").is(employeeRolesAndRoster.getLoginName())
                                        .andOperator(Criteria.where("name").is(employeeRolesAndRoster.getName())))))

                ,Employee.class,
                ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        if(employee!=null){
          List<EmployeeRolesAndRoster> employeeRolesAndRosterList =
                  mongoTemplate.find(new Query().addCriteria(Criteria.where("id").is(employeeRolesAndRoster.getId()))
                  ,EmployeeRolesAndRoster.class,ERMUtil.EMPLOYEE_ROLESANDROSTER_COLLECTION);
          if(CollectionUtils.isEmpty(employeeRolesAndRosterList)
                  || employeeRolesAndRosterList.size()>1){
            log.error("EmptyRolesAndRosterUpdateFailed:Invalid roster Id for the roster object to save");
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyRolesAndRosterUpdateFailed",
                    "Invalid roster Id for the roster object to save");
          }
          mongoTemplate.save(employeeRolesAndRoster, ERMUtil.EMPLOYEE_ROLESANDROSTER_COLLECTION);
          log.info("Roster updated successfully with id {}",employeeRolesAndRoster.getId());
          return employeeRolesAndRoster.getId();
        }
        else{
          log.error("Invalid employee Id for the roster object to save");
          ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyEmployeeForRolesAndRoster",
                  "Invalid employee Id for the roster object to save");
        }
      }
      else{
        log.error("Invalid employee Id for the roster object to save");
        ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyRolesAndRoster",
                "Invalid employee Id for the roster object to save");
      }

    }
    else{
      log.error("Empty roster object to save");
      ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmptyRolesAndRoster",
              "Invalid roster object to save");
    }

    return null;
  }

  public List<String> getRoles(LoginResponse userLogin){
    Query query = new Query();

  try {

    query.addCriteria(Criteria.where("employeeId").is(userLogin.getEmployeeId())
      .andOperator(Criteria.where("loginName").is(userLogin.getLoginName())
        .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ROSTER_STARTDATE_FILED).lte(new Date())
          .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ROSTER_ENDDATE_FILED).gte(new Date())))));
    EmployeeRolesAndRoster userRoles = mongoTemplate.findOne(query, EmployeeRolesAndRoster.class);
    if(userRoles!=null){
      return userRoles.getRoles();
    }
  }
  catch (Exception ex){
    ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "RolesAndRosterFetchUnsuccessful",
      "Exception while getting roles for the user " +userLogin.getEmployeeId()+":"+userLogin.getLoginName()+":"+
      userLogin.getName()+":"+ex.getLocalizedMessage());
  }

    return null;
  }

  public List<EmployeeRolesAndRoster> getAllRosterDetails(){
    List<EmployeeRolesAndRoster> rosters =
            mongoTemplate.findAll(EmployeeRolesAndRoster.class,ERMUtil.EMPLOYEE_ROLESANDROSTER_COLLECTION);
    return rosters;
  }

  public String deleteRosterDetails(String id){
    DeleteResult deleteResult =
            mongoTemplate.remove(new Query().addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(id)),
                    EmployeeRolesAndRoster.class,ERMUtil.EMPLOYEE_ROLESANDROSTER_COLLECTION);
    if(!deleteResult.wasAcknowledged()){
      log.error("Could not delete rosterObject with id {}",id);
      ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "DeleteRolesAndRosterUnsuccessful",
              "Could not delete rosterObject with id "+ id);
    }
    log.info("delete of roster was successful {}", id);
    return id;
  }

  private boolean isValidEmployeeRolesAndRosterRequest(EmployeeRolesAndRoster employeeRolesAndRoster){

    if(employeeRolesAndRoster==null || StringUtils.isEmpty(employeeRolesAndRoster.getEmployeeId())
            || StringUtils.isEmpty(employeeRolesAndRoster.getLoginName())
            || StringUtils.isEmpty(employeeRolesAndRoster.getName())
            || employeeRolesAndRoster.getRosterStartDate() == null){
      return false;
    }
    return true;

  }
}
