package com.jgermaine.fyp.rest.controller;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;

/**
 * Handles all RESTful operations for employees
 * 
 * @author JasonGermaine
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

	private static final Logger LOGGER = LogManager.getLogger(EmployeeController.class.getName());

	@Autowired
	private EmployeeServiceImpl employeeService;

	/**
	 * Returns list of all employees
	 * 
	 * @return employee list
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getEmployees() {
		try {
			return new ResponseEntity<List<Employee>>(employeeService.getEmployees(0), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/all/{index}", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getEmployeesForIndex(@PathVariable("index") int index) {
		try {
			return new ResponseEntity<List<Employee>>(employeeService.getEmployees(index), HttpStatus.OK);
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
			return new ResponseEntity<List<Employee>>(employeeService.getUnassignedEmployees(0), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/unassigned/{index}", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getUnassignEmployeesForIndex(@PathVariable("index") int index) {
		try {
			return new ResponseEntity<List<Employee>>(employeeService.getUnassignedEmployees(index), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * Returns list of all assigned employees
	 * 
	 * @return employee list
	 */
	@RequestMapping(value = "/assigned", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getAssignEmployees() {
		try {
			return new ResponseEntity<List<Employee>>(employeeService.getAssignedEmployees(0), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/assigned/{index}", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getAssignEmployeesForIndex(@PathVariable("index") int index) {
		try {
			return new ResponseEntity<List<Employee>>(employeeService.getAssignedEmployees(index), HttpStatus.OK);
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
			return new ResponseEntity<List<Employee>>(employeeService.getUnassignedNearEmployees(lat, lon),
					HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Retrieves an Employee for a given email
	 * <ul>
	 * <li>1. Employee exists - Employee + returns 200</li>
	 * <li>2. No Employee exists for email - return 400</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/{email:.+}", method = RequestMethod.GET)
	public ResponseEntity<Employee> getEmployee(@PathVariable("email") String email) {
		try {
			Employee emp = employeeService.getEmployee(email.toLowerCase());
			return new ResponseEntity<Employee>(emp, HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Employee>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Employee>(HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
}