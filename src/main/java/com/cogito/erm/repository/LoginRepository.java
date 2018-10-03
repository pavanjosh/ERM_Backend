package com.cogito.erm.repository;

import com.cogito.erm.dao.login.EmployeeLogin;
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
        query.addCriteria(Criteria.where("userName").is(userName));
        //query.fields().include("userName").include("employeeId");
        EmployeeLogin userLogin = mongoTemplate.findOne(query,EmployeeLogin.class);
        if(userLogin!=null){
            String dbPassword = userLogin.getPassword();
            // code to decrypt the db password
            // decrypt(dbPassword,secret);
            if(password.equals(dbPassword))
                return userLogin;
        }
        return null;

    }
}
