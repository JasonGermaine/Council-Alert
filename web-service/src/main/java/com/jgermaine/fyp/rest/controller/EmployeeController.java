package com.jgermaine.fyp.rest.controller;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;

/**
 * Handles all RESTful operations for employees
 * 
 * @author jason
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

	private static final Logger LOGGER = LogManager.getLogger(EmployeeController.class.getName());

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
	 * 
	 * @return employee list
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getEmployees() {
		try {
			LOGGER.info("Returning all employees");
			return new ResponseEntity<List<Employee>>(employeeService.getEmployees(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Returns list of all unassigned employees
	 * 
	 * @return employee list
	 */
	@RequestMapping(value = "/unassigned", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getUnassignEmployees() {
		try {
			LOGGER.info("Returning all unnassigned employees");
			return new ResponseEntity<List<Employee>>(employeeService.getUnassignedEmployees(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/assigned", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getAssignEmployees() {
		try {
			return new ResponseEntity<List<Employee>>(employeeService.getAssignedEmployees(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns a list of of reports of maximum size <i>x</i>. Reports are
	 * ordered by the distance to the latitude and longitude provided
	 * 
	 * @param lat
	 * @param lon
	 * @return list of nearest reports
	 */
	@RequestMapping(value = "/open", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getNearestUnassignedEmployees(
			@RequestParam(value = "lat", required = true) Double lat,
			@RequestParam(value = "lon", required = true) Double lon) {
		try {
			LOGGER.info("Returning employees");
			return new ResponseEntity<List<Employee>>(employeeService.getUnassignedNearEmployees(lat, lon),
					HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/{email:.+}", method = RequestMethod.GET)
	public ResponseEntity<Employee> getEmployee(@PathVariable("email") String email) {
		try {
			Employee emp = employeeService.getEmployee(email);
			return new ResponseEntity<Employee>(emp, HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Employee>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<Employee>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}