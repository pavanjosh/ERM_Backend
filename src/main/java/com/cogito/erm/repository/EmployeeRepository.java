package com.cogito.erm.repository;

import com.cogito.erm.dao.user.Employee;
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

import java.util.List;

@Repository
public class EmployeeRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger log = LoggerFactory.getLogger(EmployeeRepository.class);


    public List<Employee> getEmployees(){
        Query query = new Query();
        query.with(new Sort(Sort.Direction.ASC, ERMUtil.EMPLOYEE_NAME_FILED));
        query.addCriteria(Criteria.where("active").is(true));
        List<Employee> employees = mongoTemplate.find(query,Employee.class);
        return employees;
    }
    public List<Employee> getEmployeesWithLoginNames(){
        Query query = new Query();
        query.addCriteria(Criteria.where("active").is(true).andOperator(Criteria.where("loginName").exists(true)));
        List<Employee> employees = mongoTemplate.find(query,Employee.class,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        return employees;
    }
    public Employee updateEmployee(Employee employee){
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
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employee.getId()).andOperator(Criteria.where("active").is(true)));
        Employee employeeSearched = mongoTemplate.findOne(query, Employee.class,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        if(employeeSearched!=null){
            BeanUtils.copyProperties(employee,employeeSearched);
            mongoTemplate.save(employeeSearched);
        }
        else{
            // create and throw exception that there was no emplyee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeNotFound", "employee with id " + employee.getId()
              + " was not found");
        }
        return employeeSearched;
    }

    public Employee createEmployee(Employee employee){
        if(StringUtils.isEmpty(employee.getLoginName())
            || isUniqueLoginName(employee.getLoginName(),null))
        {
            mongoTemplate.save(employee,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
            log.debug("Employee Saved successfully");
            String id = employee.getId();
            return employee;
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
        DeleteResult remove = mongoTemplate.remove(query, Employee.class,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
        return remove.wasAcknowledged();
    }

    public List<Employee> searchEmployee(String searchTerm,String searchValue){
        Query query = new Query();
        Criteria regex = Criteria.where(searchTerm).regex(searchValue,"i");
        List<Employee> employees = mongoTemplate.find(query.addCriteria(regex), Employee.class,ERMUtil.EMPLOYEE_DETAILS_COLLECTION);
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
