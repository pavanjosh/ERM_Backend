package com.cogito.erm.service;

import com.cogito.erm.dao.login.UserLogin;
import com.cogito.erm.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<String> getRolesForUser(UserLogin userLogin){
        return employeeRepository.getRoles(userLogin);
    }
}
