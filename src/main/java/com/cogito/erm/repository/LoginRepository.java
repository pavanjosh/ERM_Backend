package com.cogito.erm.repository;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.dao.user.Employee;
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
import org.springframework.util.CollectionUtils;

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


}
