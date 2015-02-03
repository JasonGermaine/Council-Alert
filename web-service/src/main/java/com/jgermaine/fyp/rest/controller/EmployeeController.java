package com.jgermaine.fyp.rest.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jgermaine.fyp.rest.gcm.GcmOperations;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.LoginRequest;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;

/**
 * @author jason Controller to handle Employee based requests
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	private static final String PREFIX = "employee";
	private static final Logger LOGGER = LogManager
			.getLogger(EmployeeController.class.getName());

	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private ReportServiceImpl reportService;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getListUsersView(Model model) {
		LOGGER.info("Returning view to display all employees");
		model.addAttribute("employees", employeeService.getEmployees());
		return PREFIX + "/displayEmployee";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNewEmployee(Model model) {
		LOGGER.info("Returning new model view");
		Employee emp = new Employee();
		model.addAttribute("employeeForm", emp);
		return PREFIX + "/addEmployee";
	}

	/**
	 * Attempts to validate an employee form.
	 * <ul>
	 * <li>
	 * Failed validations will return the {@link #getNewEmployee(Model) new
	 * employee} page with updated error messaging</li>
	 * <li>
	 * Successful validations will persist the employee and return @link
	 * {@link #getListUsersView(Model) all employee} view with updated employee.
	 * </li>
	 * <ul>
	 * 
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
			LOGGER.info("Returning new model view with error messages");
			return PREFIX + "/addEmployee";
		} else {
			LOGGER.info("Valid Employee - persistenting object");
			employeeService.addEmployee(employee);
			return "redirect:/employee/";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<String> attemptLogin(@RequestBody LoginRequest data) {
		String email = data.getEmail();
		String password = data.getPassword();
		String deviceId = data.getDeviceId();
		LOGGER.info("Received Login Attempt: " + email + " " + password + " " + deviceId);
		Employee employee = employeeService.getEmployee(email);
		if (employee != null && password.equals(employee.getPassword())) {
			if (deviceId != null && (employee.getDeviceId() == null || !employee.getDeviceId().equals(deviceId))) {
				employee.setDeviceId(deviceId);
				employeeService.updateEmployee(employee);
			}
			return new ResponseEntity<String>(HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping("/task")
	public String assignTask(
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "id", required = true) String reportId) throws IOException {
		Employee employee = employeeService.getEmployee(email);
		employee.setReport(reportService.getReport(Integer.parseInt(reportId)));
		employeeService.updateEmployee(employee);
		GcmOperations.sendReportIdAsNotification(reportId, employee.getDeviceId());
		return "redirect:/employee/";
	}
	
	@RequestMapping(value = "/slackers", method = RequestMethod.GET)
	public String getUnassignedWorkers(Model model) {
		LOGGER.info("Returning view to display all employees with unassigned jobs");
		model.addAttribute("employees",
				employeeService.getUnassignedEmployees());
		return PREFIX + "/displayUnassignedWorkers";
	}

	@RequestMapping(value = "/assign", method = RequestMethod.GET)
	public String assignWorker(Model model,
			@RequestParam(value = "email", required = true) String email)
			throws IOException {
		GcmOperations.sendNotifcation();
		LOGGER.info("Returning view to display all employees with unassigned jobs");
		return PREFIX + "/displayUnassignedWorkers";
	}
}