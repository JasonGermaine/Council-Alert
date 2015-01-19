package com.jgermaine.fyp.rest.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	private static final Logger LOGGER = LogManager
			.getLogger(EmployeeController.class.getName());

	@Autowired
	private EmployeeServiceImpl employeeService;

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNewEmployee(Model model) {
		Employee emp = new Employee();
		model.addAttribute("employeeForm", emp);
		return "addEmployee";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String registerEmployee(
			@Valid @ModelAttribute("employeeForm") Employee employee,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "addEmployee";
		} else {
			employeeService.addEmployee(employee);
			return "redirect:/employee/display";
		}
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public String getListUsersView(Model model) {
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
	public Boolean getPage(
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password) {
		Employee employee = employeeService.getEmployee(email);
		if (employee != null && password.equals(employee.getPassword())) {
			return true;
		} else {
			return false;
		}
	}

	@RequestMapping(value = "/slackers", method = RequestMethod.GET)
	public String getUnassignedWorkers(Model model) {
		model.addAttribute("employees",
				employeeService.getUnassignedEmployees());
		return "displayUnassignedWorkers";
	}
}