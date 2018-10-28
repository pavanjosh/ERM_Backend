package com.cogito.erm.repository;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.model.authentication.LoginResponse;
import com.cogito.erm.util.ERMUtil;
import com.mongodb.client.result.DeleteResult;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${erm.encryptor.password}")
    private String encryptor;

    private static final Logger log = LoggerFactory.getLogger(LoginRepository.class);

    public LoginResponse checkCredentials(String userName,String password){
        Query query = new Query();
        //String encryptedPassword = getEncryptedPassword(password);
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


        // Basic validation to see if login name and password is not null.
        // If it is null then return error
        if(employeeLogin==null || StringUtils.isEmpty(employeeLogin.getLoginName())
                || StringUtils.isEmpty(employeeLogin.getPassword())){
            log.error("Login name or password cannot be null");
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginNameOrPasswordEmpty",
                    "Login name or password cannot be null");
        }
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
                    log.info("Encrypting password in Progress...");
                    //String encryptedPassword = getEncryptedPassword(employeeLogin.getPassword());
                    //employeeLogin.setPassword(encryptedPassword);
                    log.info("Encrypting password successful");
                    mongoTemplate.save(employeeLogin);
                    return employeeLogin.getEmployeeId();
                }
                else{
                    log.error("Login name already taken by a employee, please find an another login name");
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
            log.error("Mandatory id filed missing for the update employee login {} ",employeeLogin);
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UpdateEmployeeLoginCredentialsIdNotFound", "Mandatory id filed "
              + "missing for the update employee login" );
        }
        if(!isUniqueLoginName(employeeLogin.getLoginName(),employeeLogin.getId())){
            log.error("Login name already taken by a employee, please find an another login name {} ",employeeLogin);
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginNameAlreadyTaken",
              "Login name already taken by a employee, please find an another login name");
        }
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_EMPLOYEEID_FILED).is(employeeLogin.getEmployeeId()).andOperator(Criteria.where("active").is(true)));
        Employee employeeSearched = mongoTemplate.findOne(query, Employee.class,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        if(employeeSearched!=null){
            String encryptedPassword = getEncryptedPassword(employeeLogin.getPassword());
            EmployeeLogin updateEmployeeLogin = mongoTemplate
              .findOne(new Query().addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeLogin.getId()))
                , EmployeeLogin.class, ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
            BeanUtils.copyProperties(employeeLogin,updateEmployeeLogin);
            updateEmployeeLogin.setPassword(encryptedPassword);
            mongoTemplate.save(updateEmployeeLogin,ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
        }
        else{
            // create and throw exception that there was no employee with id found.
            log.error("Employee with given employeeId {} was not found or is NOT ACTIVE {}",employeeLogin.getEmployeeId(),employeeLogin);
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeLoginNotFound", "employee with id "
              + employeeLogin.getEmployeeId() + " with login name " + employeeLogin.getLoginName()
              + " was not found in the employee details data OR is NOT active");
        }
        return employeeSearched.getId();
    }

    public EmployeeLogin getLoginCredentials(String employeeId,String loginName){
        if(StringUtils.isEmpty(employeeId)){
            // create and throw exception that there was no employee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeLoginNotFound", "Mandatory id field "
              + "for the get employee login" );
        }
        if(StringUtils.isEmpty(loginName)){
            // create and throw exception that there was no employee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeLoginNameFound", "Mandatory loginName field "
              + " the get employee login" );
        }
        Query query = new Query();
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_EMPLOYEEID_FILED).is(employeeId).andOperator(Criteria.where("loginName").is(loginName)));
        EmployeeLogin employeeLogin = mongoTemplate.findOne(query, EmployeeLogin.class, ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
        log.info("Password decryption in progress...");
        String decryptedPassword = decryptPassword(employeeLogin.getPassword());
        employeeLogin.setPassword(decryptedPassword);
        log.info("Password decryption successful");
        return employeeLogin;
    }

    public String deleteLoginCredentials(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(id));
        // first find the employee associated with this id
        // delete the login name for that employee and then delete the login credentials
        EmployeeLogin employeeLogin = mongoTemplate.findOne(query,EmployeeLogin.class,ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
        if(employeeLogin!=null){
            String employeeId = employeeLogin.getEmployeeId();
            if(!StringUtils.isEmpty(employeeId)){
                Query employeeQuery = new Query();
                employeeQuery.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeId));
                Employee employee =mongoTemplate.findOne(employeeQuery,Employee.class,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
                if(employee!=null){
                    employee.setLoginName("");
                    mongoTemplate.save(employee,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
                }
                else{
                    ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeNotFound",
                            "There is no referenced employee for the login credentials id " + id
                                    +" and employee id "+employeeId);
                }
            }
            else{
                ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeIdNotFound",
                        "There is no referenced employee for the login credentials id " + id );
            }

        }
        else{
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "getEmployeeLoginNotFound",
                    "given id did not match any employee login credentials record" );
        }
        DeleteResult remove = mongoTemplate.remove(query, EmployeeLogin.class,ERMUtil.EMPLOYEE_LOGIN_COLLECTION);
        log.info("Employee login deleted successfully {}",id);
        return (remove.wasAcknowledged())?id:null;
    }

    private boolean isUniqueLoginName(String loginName,String id){

        List<Employee> employees = mongoTemplate
          .find(new Query().addCriteria(Criteria.where("loginName").is(loginName).
              andOperator(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).ne(id))), Employee.class,
            ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        if (CollectionUtils.isEmpty(employees)) {
            log.debug("Login name is unique");
            return true;
        }
        else{
            log.error("Login name already taken by a employee, please find some other login name");
        }

        return false;
    }

    private String getEncryptedPassword(String password){
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(encryptor);
        String encrypt = textEncryptor.encrypt(password);
        return encrypt;
    }

    private String decryptPassword(String password) {
        StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(encryptor);
        String decrypt = textEncryptor.decrypt(password);
        return decrypt;
    }
}
