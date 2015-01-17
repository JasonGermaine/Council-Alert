package com.jgermaine.fyp.rest.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	private static final Logger LOGGER = LogManager
			.getLogger(EmployeeController.class.getName());

	@Autowired
	private EmployeeServiceImpl employeeService;

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView getRegisterView(Model m) {
		return new ModelAndView("addEmployee", "employeeForm", new Employee());
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView postUser(@Valid Employee employee, BindingResult result) {
		ModelAndView model;
		if (result.hasErrors()) {
			model = new ModelAndView("addEmployee", "employeeForm", employee);
		} else {
			employeeService.addEmployee(employee);
			model = new ModelAndView();
			model.setViewName("displayEmployee");
			model.addObject("employees", employeeService.getEmployees());
		}

		return model;
	}

	@RequestMapping("/display")
	public ModelAndView getListUsersView() {
		ModelAndView model = new ModelAndView();
		model.setViewName("displayEmployee");
		model.addObject("employees", employeeService.getEmployees());
		return model;
	}

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

	@RequestMapping("/slackers")
	public ModelAndView getUnassignedWorkers() {
		ModelAndView model = new ModelAndView();
		model.setViewName("displayUnassignedWorkers");
		model.addObject("employees", employeeService.getUnassignedEmployees());
		return model;
	}
}