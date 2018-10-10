package com.cogito.erm.repository;

import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.model.authentication.LoginResponse;
import com.cogito.erm.util.ERMUtil;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Repository
public class EmployeeRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(EmployeeRepository.class);

    public List<String> getRoles(LoginResponse userLogin){
        Query query = new Query();
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(userLogin.getEmployeeId())
                .andOperator(Criteria.where(ERMUtil.EMPLOYEE_NAME_FILED).is(userLogin.getLoginName())
                .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ROSTER_STARTDATE_FILED).gte(Instant.now()))));

        EmployeeRolesAndRoster userRoles = mongoTemplate.findOne(query, EmployeeRolesAndRoster.class);
        if(userRoles!=null){
            return userRoles.getRole();
        }
        return null;
    }

    public List<Employee> getEmployees(){
        Query query = new Query();
        query.with(new Sort(Sort.Direction.ASC, ERMUtil.EMPLOYEE_NAME_FILED));
        List<Employee> employees = mongoTemplate.find(query,Employee.class);
        return employees;
    }

    public String updateEmployee(Employee employee){
        Query query = new Query();
        if(StringUtils.isEmpty(employee.getId())){
          // create and throw exception that there was no employee with id found.
          ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UpdateEmployeeIdNotFound", "Mandatory id filed "
            + "missing for the update employee" );
        }
        if(!isUniqueLoginName(employee.getLoginName(),employee.getId())){
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginNameAlreadyTaken",
              "Login name already taken by a employee, please find an another login name");
        }
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employee.getId()));
        Employee employeeSearched = mongoTemplate.findOne(query, Employee.class);
        if(employeeSearched!=null){
            BeanUtils.copyProperties(employee,employeeSearched);
            mongoTemplate.save(employeeSearched);
        }
        else{
            // create and throw exception that there was no emplyee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeNotFound", "employee with id " + employee.getId()
              + " was not found");
        }
        return employeeSearched.getId();
    }

    public String createEmployee(Employee employee){
        if(StringUtils.isEmpty(employee.getLoginName())
            || isUniqueLoginName(employee.getLoginName(),null))
        {
            mongoTemplate.save(employee);
            log.debug("Employee Saved successfully");
            String id = employee.getId();
            return id;
        }
        else {
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "LoginNameAlreadyTaken",
              "Login name already taken by a employee, please find an another login name");
        }

       return null;
    }

    public boolean deleteEmployees(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(id));
        DeleteResult remove = mongoTemplate.remove(query, Employee.class);
        return remove.wasAcknowledged();
    }

    public List<Employee> searchEmployee(String searchTerm,String searchValue){
        Query query = new Query();
        Criteria regex = Criteria.where(searchTerm).regex(searchValue,"i");
        List<Employee> employees = mongoTemplate.find(query.addCriteria(regex), Employee.class);
        return employees;
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
