package com.jgermaine.fyp.rest.controller;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;


/**
 * Handles all RESTful operations for employees
 * @author jason
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

	private static final Logger LOGGER = LogManager
			.getLogger(EmployeeController.class.getName());

	@Autowired
	private CouncilAlertUserDetailsService councilAlertUserService;
	
	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private ReportServiceImpl reportService;
	
	@Autowired
	private UserServiceImpl userService;
	
	/**
	 * Returns list of all employees
	 * @return employee list
	 */
	@RequestMapping(value = "/", method=RequestMethod.GET)
	public ResponseEntity<List<Employee>> getEmployees() {
		LOGGER.info("Returning all employees");
		return new ResponseEntity<List<Employee>> (employeeService.getEmployees(), HttpStatus.OK);
	}

	/**
	 * Returns list of all unassigned employees
	 * @return employee list
	 */
	@RequestMapping(value = "/unassigned", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getUnassignEmployees() {
		LOGGER.info("Returning all unnassigned employees");
		return new ResponseEntity<List<Employee>> (employeeService.getUnassignedEmployees(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/assigned", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getAssignEmployees() {
		return new ResponseEntity<List<Employee>> (employeeService.getAssignedEmployees(), HttpStatus.OK);
	}
	
	/**
	 * Returns a list of of reports of maximum size <i>x</i>.
	 * Reports are ordered by the distance to the latitude and longitude provided
	 * @param lat
	 * @param lon
	 * @return list of nearest reports
	 */
	@RequestMapping(value="/open", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getNearestUnassignedEmployees (
			@RequestParam(value = "lat", required = true) Double lat,
			@RequestParam(value = "lon", required = true) Double lon) {
		LOGGER.info("Returning employees");
		return new ResponseEntity<List<Employee>>(employeeService.getUnassignedNearEmployees(lat, lon), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/emp", method = RequestMethod.GET)
	public ResponseEntity<Employee> getEmployee(@RequestParam(value = "email", required = true) String email) {
		Employee emp = employeeService.getEmployee(email);
		if (emp != null) {			
			return new ResponseEntity<Employee>(emp, HttpStatus.OK);
		} else {
			return new ResponseEntity<Employee>(HttpStatus.BAD_REQUEST);
		}
	}
}