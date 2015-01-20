package com.jgermaine.fyp.rest.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jgermaine.fyp.rest.gcm.GcmOperations;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;


/**
 * @author jason
 * Controller to handle Employee based requests
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	private static final Logger LOGGER = LogManager
			.getLogger(EmployeeController.class.getName());

	@Autowired
	private EmployeeServiceImpl employeeService;

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNewEmployee(Model model) {
		LOGGER.debug("Returning new model view");
		Employee emp = new Employee();
		model.addAttribute("employeeForm", emp);
		return "addEmployee";
	}

	/**
	 * Attempts to validate an employee form.
	 * <ul>
	 * <li>
	 * Failed validations will return the {@link #getNewEmployee(Model) new employee}
	 * page with updated error messaging
	 * </li>
	 * <li>
	 * Successful validations will persist the employee and return @link {@link #getListUsersView(Model)
	 * all employee} view with updated employee.
	 * </li> 
	 * <ul>
	 * @param employee
	 * @param result
	 * @param model
	 * @return view
	 */
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String registerEmployee(
			@Valid @ModelAttribute("employeeForm") Employee employee,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			LOGGER.debug("Returning new model view with error messages");
			return "addEmployee";
		} else {
			LOGGER.debug("Valid Employee - persistenting object");
			employeeService.addEmployee(employee);
			return "redirect:/employee/display";
		}
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public String getListUsersView(Model model) {
		LOGGER.debug("Returning view to display all employees");
		model.addAttribute("employees", employeeService.getEmployees());
		return "displayEmployee";
	}
	
	@RequestMapping(value = "/assignAndDisplay", method = RequestMethod.GET)
	public String getListUsersViewAndSendNotification(Model model) throws IOException {
		GcmOperations.sendNotifcation();
		model.addAttribute("employees", employeeService.getEmployees());
		return "displayEmployee";
	}

	@ResponseBody
	@RequestMapping("/login")
	public ResponseEntity<String> getPage(
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password) {
		Employee employee = employeeService.getEmployee(email);
		if (employee != null && password.equals(employee.getPassword())) {
			return new ResponseEntity<String>(HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/slackers", method = RequestMethod.GET)
	public String getUnassignedWorkers(Model model) {
		LOGGER.debug("Returning view to display all employees with unassigned jobs");
		model.addAttribute("employees",
				employeeService.getUnassignedEmployees());
		return "displayUnassignedWorkers";
	}
}