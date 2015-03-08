package com.jgermaine.fyp.rest.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.gcm.GcmOperations;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private static final Logger LOGGER = LogManager
			.getLogger(AdminController.class.getName());

	@Autowired
	private CouncilAlertUserDetailsService councilAlertUserService;
	
	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private ReportServiceImpl reportService;
	
	@Autowired
	private UserServiceImpl userService;
		
	/**
	 * Creates a new Employee
	 * There are 3 possible outputs
	 * <ul>
	 * <li>1. Employee is valid and unique - returns 201</li>
	 * <li>2. Employee already exists in DB - returns 409</li>
	 * <li>3. Invalid Employee passed - returns 400</li>
	 * </ul>
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> createEmployee(@RequestBody @Valid Employee employee) {
		if (userService.getUser(employee.getEmail())  == null ) {				
			employeeService.addEmployee(employee);
			councilAlertUserService.createNewUser(employee);
			LOGGER.info("Creating Employee: " + employee.getEmail());
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} else {
			LOGGER.warn("Employee already exists " + employee.getEmail());
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		}
	}			

	/**
	 * Assigns a report with the given id to an Employee with the given email
	 * There are 3 possible outputs
	 * <ul>
	 * <li>1. Employee and Report exist - returns 200</li>
	 * <li>2. Employee or Report ids do not exist - returns 400</li>
	 * </ul>
	 * @return HttpResponse
	 */
	@RequestMapping("/assign")
	public ResponseEntity<String> assignReportToEmployee(
		@RequestParam(value = "email", required = true) String email,
		@RequestParam(value = "id", required = true) String reportId) throws IOException {
	
		Report report = reportService.getReport(Integer.parseInt(reportId));
		Employee employee = employeeService.getEmployee(email);
		
		if (report != null || employee != null) {
			report.setEmployee(employee);
			reportService.updateReport(report);
			
			// New Thread spawned for GCM so it does no block response
			GcmOperations.sendReportIdAsNotification(reportId, employee.getDeviceId());
			
			LOGGER.info(String.format("Assigned Employee: %s to Report: %s",
					employee.getEmail(), report.getId()));
			return new ResponseEntity<String>(HttpStatus.OK);
		} else {
			LOGGER.warn("Invalid Params for request: " + email + " & " + reportId);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping("/ping")
	public ResponseEntity<String> getMessage() {
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
