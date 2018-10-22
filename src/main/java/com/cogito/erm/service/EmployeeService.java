package com.cogito.erm.service;

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


    public List<Employee> getEmployees(){
        List<Employee> employees = employeeRepository.getEmployees();
        logger.info("List of employees are {}",employees);
        return employees;
    }

    public Employee updateEmployee(Employee updateEmployee){
        Employee updateEmployeeResult = employeeRepository.updateEmployee(updateEmployee);
        logger.info("update Employee result {}",updateEmployeeResult);
        return updateEmployeeResult;

    }

    public Employee createEmployee(Employee employee){
        Employee createEmployeeResult = employeeRepository.createEmployee(employee);
        logger.info("Create Employee result {}",createEmployeeResult);
        return createEmployeeResult;
    }

    public boolean deleteEmployee(String id){
        return employeeRepository.deleteEmployees(id);

    }

    public List<Employee> searchEmployee(String searchTerm,String searchValue){
        List<Employee> employees = employeeRepository.searchEmployee(searchTerm, searchValue);
        return employees;
    }

    public List<Employee> getEmployeesWithLoginNames(){
        List<Employee> employees = employeeRepository.getEmployeesWithLoginNames();
        return employees;
    }
}
