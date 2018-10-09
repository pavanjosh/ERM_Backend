package com.cogito.erm.repository;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.util.ERMUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LoginRepository {

    @Autowired
    private MongoTemplate mongoTemplate;


    public EmployeeLogin checkCredentials(String userName,String password){

        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(userName).andOperator(Criteria.where("password").is(password)));
        //query.fields().include("userName").include("employeeId");
        EmployeeLogin userLogin = mongoTemplate.findOne(query,EmployeeLogin.class);
        // after the emplyee login details is found, find the employee in employee table
        // if found check the status, if active then login is successfull else not successfull
        if(userLogin !=null){
            String employeeId = userLogin.getEmployeeId();
            Query employeeQuery = new Query().addCriteria(Criteria.where(ERMUtil.EMPLOYEE_ID_FILED).is(employeeId)
              .andOperator(Criteria.where(ERMUtil.EMPLOYEE_ACTIVE_FILED).is(true)));
            Employee employeeDetails = mongoTemplate.findOne(employeeQuery, Employee.class, "employeeDetails");
            if(employeeDetails!=null)
                return userLogin;
        }
        return null;

    }
}
