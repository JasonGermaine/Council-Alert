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
import com.jgermaine.fyp.rest.model.LoginRequest;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.model.dao.UserDao;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/login")
public class LoginController {

	private static final Logger LOGGER = LogManager
			.getLogger(LoginController.class.getName());
	
	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private UserServiceImpl userService;

	/**
	 * Attempts to log in a user
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/", method = RequestMethod.POST)
	public ResponseEntity<User> attemptLogin(@RequestBody LoginRequest data) {		
		String email = data.getEmail();
		String password = data.getPassword();
		LOGGER.info("Received Login Attempt: " + email + " " + password);
		
		User user = userService.getUser(email);		
		if (user == null || !user.getPassword().equals(password)) {
			LOGGER.warn("Failed Login for " + email + " : " + password);
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		} else {
			String deviceId = data.getDeviceId();
			if (deviceId != null && user instanceof Employee) {				
				updateEmployee((Employee) user, deviceId);
			}	
			LOGGER.info("Successful Login for " + email + " : " + password);
			return new ResponseEntity<User>(user, HttpStatus.ACCEPTED) ;
		}
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
