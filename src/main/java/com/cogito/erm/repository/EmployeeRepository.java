package com.cogito.erm.repository;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.dao.user.EmployeeRoles;
import com.cogito.erm.util.ERMUtil;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> getRoles(EmployeeLogin userLogin){
        Query query = new Query();
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(userLogin.getEmployeeId())
                .andOperator(Criteria.where(ERMUtil.EMPLOYEE_NAME_FILED).is(userLogin.getName())));

        EmployeeRoles userRoles = mongoTemplate.findOne(query,EmployeeRoles.class);
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

    public String updateEmployee(String id, Employee employee){
        Query query = new Query();
        query.addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(id));
        Employee employeeSearched = mongoTemplate.findOne(query, Employee.class);
        if(employeeSearched!=null){
            DeleteResult remove = mongoTemplate.remove(employeeSearched);
            if(remove.wasAcknowledged())
            {
                mongoTemplate.save(employee);
            }
            else{
                // create and throw service exception
                ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeNotFound", "Attempt to delete the "
                  + "employee with id " + id + " failed, hence ans exception is created");
            }
        }
        else{
            // create and throw exception that there was no emplyee with id found.
            ERMUtil.createAndThrowException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EmployeeNotFound", "employee with id " + id
              + " was not found");
        }
        return id;
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

}
