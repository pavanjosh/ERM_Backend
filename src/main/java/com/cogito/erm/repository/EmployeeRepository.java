package com.cogito.erm.repository;

import com.cogito.erm.dao.login.UserLogin;
import com.cogito.erm.dao.user.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> getRoles(UserLogin userLogin){
        Query query = new Query();
        query.addCriteria(Criteria.where("employeeId").is(userLogin.getEmployeeId())
                .andOperator(Criteria.where("userName").is(userLogin.getUserName())));

        UserRoles userRoles = mongoTemplate.findOne(query,UserRoles.class);
        if(userRoles!=null){
            return userRoles.getRole();
        }
        return null;
    }
}
