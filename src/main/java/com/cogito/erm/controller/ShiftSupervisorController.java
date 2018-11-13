package com.cogito.erm.controller;

import com.cogito.erm.dao.form.ShiftSupervisorForm;
import com.cogito.erm.service.ShiftSupervisorFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShiftSupervisorController {

    @Autowired
    private ShiftSupervisorFormService shiftSupervisorFormService;

    @RequestMapping(value = "/shiftSuperVisor/form", method = RequestMethod.GET)
    public ResponseEntity<ShiftSupervisorForm> getShiftSupervisorForm(){
        ShiftSupervisorForm shiftSupervisorForm = shiftSupervisorFormService.createSupervisorRequestForm();
        return new ResponseEntity<ShiftSupervisorForm>(shiftSupervisorForm, HttpStatus.OK);

    }
}
