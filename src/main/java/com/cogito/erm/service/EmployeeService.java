package com.cogito.erm.service;

import com.cogito.erm.dao.login.EmployeeLogin;
import com.cogito.erm.dao.user.Employee;
import com.cogito.erm.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<String> getRolesForEmployee(EmployeeLogin userLogin){
        return employeeRepository.getRoles(userLogin);
    }

    public List<Employee> getEmployees(){
        List<Employee> employees = employeeRepository.getEmployees();
        return employees;
    }

    public String updateEmployee(String id,Employee updateEmployee){
        return employeeRepository.updateEmployee(id, updateEmployee);

    }

    public String createEmployee(Employee employee){
        return employeeRepository.createEmployee(employee);
    }

    public boolean deleteEmployee(String id){
        return employeeRepository.deleteEmployees(id);

    }
}
