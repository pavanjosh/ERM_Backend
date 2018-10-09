package com.cogito.erm.repository;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.dao.user.EmployeeRolesAndRoster;
import com.cogito.erm.util.ERMUtil;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;

@Repository
public class EmployeeRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> getRoles(EmployeeLogin userLogin){
        Query query = new Query();
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(userLogin.getEmployeeId())
                .andOperator(Criteria.where(ERMUtil.EMPLOYEE_NAME_FILED).is(userLogin.getName())
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
        mongoTemplate.save(employee);
        String id = employee.getId();
        return id;
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

}
