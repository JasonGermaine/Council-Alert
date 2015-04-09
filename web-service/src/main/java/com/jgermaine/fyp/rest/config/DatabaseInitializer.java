package com.jgermaine.fyp.rest.config;

import javax.annotation.PostConstruct;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.jgermaine.fyp.rest.controller.AdminController;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;

@Component
public class DatabaseInitializer {

	private static final Logger LOGGER = LogManager.getLogger(DatabaseInitializer.class.getName());

	@Autowired
	private CouncilAlertUserDetailsService councilAlertUserService;

	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private ReportServiceImpl reportService;

	@Autowired
	private UserServiceImpl userService;

	@PostConstruct
	public void postConstruct() {
		Employee employee = getDefaultAdminEmp();
		try {
			employeeService.addEmployee(employee);
			councilAlertUserService.createNewUser(employee);
			LOGGER.info("Creating Employee: " + employee.getEmail());
		} catch (Exception e) {
			LOGGER.info("Employee Already Created: " + employee.getEmail());
		}
	}

	private Employee getDefaultAdminEmp() {
		Employee employee = new Employee();
		employee.setEmail("admin@council-alert.com");
		employee.setPassword("admin");
		employee.setFirstName("Jason");
		employee.setLastName("Germaine");
		employee.setPhoneNum("123-Council-Alert");
		employee.setLatitude(53.2884615);
		employee.setLongitude(-6.3525261);
		return employee;
	}
}
