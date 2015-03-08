package com.jgermaine.fyp.rest.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.UserRequest;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger LOGGER = LogManager
			.getLogger(UserController.class.getName());
	
	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private UserServiceImpl userService;

	/**
	 * Retrieves a user after login
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/retrieve", method=RequestMethod.POST)
	public ResponseEntity<User> attemptLogin(
			@RequestBody UserRequest data) {		
		
		String email = data.getEmail();
		User user = userService.getUser(email);		
		String deviceId = data.getDeviceId();
		if (deviceId != null && user instanceof Employee) {				
			updateEmployee((Employee) user, deviceId);
		}	
		return new ResponseEntity<User>(user, HttpStatus.OK) ;
	}	
	
	/**
	 * Updates Employees Device Key
	 * @param employee
	 * @param deviceId
	 */
	private void updateEmployee(Employee employee, String deviceId) {		
		if ((employee.getDeviceId() == null || !employee.getDeviceId().equals(deviceId))) {
			LOGGER.info("Updating employee: " + employee.getEmail() 
					+ " device key to: " + deviceId);
			employee.setDeviceId(deviceId);
			employeeService.updateEmployee(employee);
		}
	}
}
