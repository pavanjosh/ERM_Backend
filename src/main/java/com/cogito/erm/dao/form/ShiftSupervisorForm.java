package com.cogito.erm.dao.form;

import com.cogito.erm.dao.Items;
import com.cogito.erm.dao.Location;
import com.cogito.erm.dao.Vessels;
import com.cogito.erm.dao.user.Employee;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection="shiftSupervisorForm")
public class ShiftSupervisorForm {

    private String employeeId;
    private String name;
    private Vessels vessels;
    private Location locations;
    private Date date;
    private List<Employee> dmcoEmployeeList;
    private List<Items> itemList;


}
