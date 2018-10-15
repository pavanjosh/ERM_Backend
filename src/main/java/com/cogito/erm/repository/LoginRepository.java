package com.cogito.erm.repository;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.model.authentication.LoginResponse;
import com.cogito.erm.util.ERMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Repository
public class LoginRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(LoginRepository.class);

    public LoginResponse checkCredentials(String userName,String password){
        Query query = new Query();
        query.addCriteria(Criteria.where("loginName").is(userName).andOperator(Criteria.where("password").is(password)));
        //query.fields().include("userName").include("employeeId");
        EmployeeLogin userLogin = mongoTemplate.findOne(query,EmployeeLogin.class);
        // after the emplyee login details is found, find the employee in employee table
        // if found check the status, if active then login is successful else not successful
        if(userLogin !=null){
            String employeeId = userLogin.getEmployeeId();
            Query employeeQuery = new Query().addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeId)
              .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ACTIVE_FILED).is(true),Criteria.where("loginName").is(userName)));
            Employee employeeDetails = mongoTemplate.findOne(employeeQuery, Employee.class, ERMUtil.EMPLOYEE_DETAILS_COLLECTION);

            if(employeeDetails!=null) {
                log.debug("Login successful for the user {} ", userName);
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setEmployeeId(employeeDetails.getId());
                loginResponse.setLoginName(employeeDetails.getLoginName());
                loginResponse.setName(employeeDetails.getName());
                userLogin.setLastLogin(new Date());
                mongoTemplate.save(userLogin,ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
                return loginResponse;
            }
            else{
                log.error("Login unsuccessful for user {} ", userName);
                ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginUnsuccessful",
                  "Please provide valid credentials");
            }
        }
        return null;

    }
    public String createLoginCredentials(EmployeeLogin employeeLogin){

        // check if employee id is supplied, if yes check if the user exists
        // encrypt the password and save in table
        // update created date
        if(employeeLogin.getEmployeeId()!=null){
            String employeeId = employeeLogin.getEmployeeId();
            Query employeeQuery = new Query().addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeId)
              .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ACTIVE_FILED).is(true),Criteria.where("loginName").is(employeeLogin.getLoginName())));
            Employee employeeDetails = mongoTemplate.findOne(employeeQuery, Employee.class, ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
            if(employeeDetails!=null){
                List<EmployeeLogin> loginNamesList = mongoTemplate
                  .find(new Query().addCriteria(Criteria.where("loginName").is(employeeLogin.getLoginName())),
                    EmployeeLogin.class, ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
                if(CollectionUtils.isEmpty(loginNamesList)) {
                    employeeLogin.setCreatedDate(new Date());
                    mongoTemplate.save(employeeLogin);
                    return employeeLogin.getEmployeeId();
                }
                else{
                    ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginNameAlreadyTaken",
                      "Login name already taken by a employee, please find an another login name");
                }
            }
            else{
                log.error("Create Login credentials unsuccessful, please provide a valid employee id");
                ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginCredentialsRegistrationUnsuccessful",
                  "Create Login credentials unsuccessful, please provide a valid employee id or corresponding login name");
            }
        }
        else
        {
            log.error("Create Login credentials unsuccessful, please provide a valid employee id");
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginCredentialsRegistrationUnsuccessful",
              "Create Login credentials unsuccessful, please provide a valid employee id");

        }
        return null;
    }


    public String updateLoginCredentials(EmployeeLogin employeeLogin){
        Query query = new Query();
        if(StringUtils.isEmpty(employeeLogin.getId())){
            // create and throw exception that there was no employee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UpdateEmployeeLoginIdNotFound", "Mandatory id filed "
              + "missing for the update employee login" );
        }
        if(!isUniqueLoginName(employeeLogin.getLoginName(),employeeLogin.getId())){
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginNameAlreadyTaken",
              "Login name already taken by a employee, please find an another login name");
        }
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeLogin.getId()));
        Employee employeeSearched = mongoTemplate.findOne(query, Employee.class);
        if(employeeSearched!=null){
            BeanUtils.copyProperties(employeeLogin,employeeSearched);
            mongoTemplate.save(employeeSearched);
        }
        else{
            // create and throw exception that there was no emplyee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeLoginNotFound", "employee with id "
              + employeeLogin.getId()
              + " was not found");
        }
        return employeeSearched.getId();
    }

    public EmployeeLogin getLoginCredentials(String employeeId){
        if(StringUtils.isEmpty(employeeId)){
            // create and throw exception that there was no employee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "getEmployeeLoginNotFound", "Mandatory id field "
              + "missing for the get employee login" );
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("employeeId").is(employeeId));
        EmployeeLogin employeeLogin = mongoTemplate.findOne(query, EmployeeLogin.class, ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
        return employeeLogin;

    }

    private boolean isUniqueLoginName(String loginName,String id){

        List<Employee> employees = mongoTemplate
          .find(new Query().addCriteria(Criteria.where("loginName").is(loginName).
              andOperator(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).ne(id))), Employee.class,
            ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        if (CollectionUtils.isEmpty(employees)) {
            return true;
        }
        else{
            log.error("Login name already taken by a employee, please find some other login name");
        }

        return false;
    }
}
