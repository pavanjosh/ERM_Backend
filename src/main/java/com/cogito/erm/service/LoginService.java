package com.cogito.erm.service;


import com.cogito.erm.dao.login.UserLogin;
import com.cogito.erm.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    public UserLogin login(String userName, String password){

        return loginRepository.checkCredentials(userName, password);
    }
}
