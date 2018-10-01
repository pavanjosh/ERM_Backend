package com.cogito.erm.repository;

import com.cogito.erm.dao.login.UserLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class LoginRepository {

    @Autowired
    private MongoTemplate mongoTemplate;


    public UserLogin checkCredentials(String userName,String password){

        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        //query.fields().include("userName").include("employeeId");
        UserLogin userLogin = mongoTemplate.findOne(query,UserLogin.class);
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
