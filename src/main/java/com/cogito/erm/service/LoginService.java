package com.cogito.erm.service;


import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LoginService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LoginRepository loginRepository;

    public EmployeeLogin login(String userName, String password){

        logger.debug("Check credentials method");
        return loginRepository.checkCredentials(userName, password);
    }
}
