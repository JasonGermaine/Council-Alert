package com.jgermaine.fyp.rest.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.gcm.TaskManager;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.EmployeeUpdateRequest;
import com.jgermaine.fyp.rest.model.PasswordChangeRequest;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.model.UserRequest;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
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

	@Autowired
	private CitizenServiceImpl citizenService;

	/**
	 * Creates a new Employee There are 3 possible outputs
	 * <ul>
	 * <li>1. Employee is valid and unique - returns 201</li>
	 * <li>2. Employee already exists in DB - returns 409</li>
	 * <li>3. Invalid Employee passed - returns 400</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> createEmployee(
			@RequestBody @Valid Employee employee) {
		if (userService.getUser(employee.getEmail()) == null) {
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
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping("/assign")
	public ResponseEntity<String> assignReportToEmployee(
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "id", required = true) String reportId)
			throws IOException {

		Report report = reportService.getReport(Integer.parseInt(reportId));
		Employee employee = employeeService.getEmployee(email);

		if (report != null || employee != null) {
			report.setEmployee(employee);
			reportService.updateReport(report);

			// New Thread spawned for GCM so it does no block response
			TaskManager.sendReportIdAsNotification(reportId, employee);

			LOGGER.info(String.format("Assigned Employee: %s to Report: %s",
					employee.getEmail(), report.getId()));
			return new ResponseEntity<String>(HttpStatus.OK);
		} else {
			LOGGER.warn("Invalid Params for request: " + email + " & "
					+ reportId);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/employee", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeEmployee(
			@RequestParam(value = "email", required = true) String email) {
		LOGGER.info("Deleting email - " + email);
		Employee emp = employeeService.getEmployee(email);
		if (emp != null) {
			employeeService.removeEmployee(emp);
			if (emp.getReport() != null) {
				Report r = emp.getReport();
				r.setEmployee(null);
				reportService.updateReport(r);
			}
			councilAlertUserService.removeUser(emp);
			; 
			return new ResponseEntity<String>(HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/employee/{email:.+}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateEmployee(@PathVariable("email") String email, 
			@RequestBody @Valid EmployeeUpdateRequest request) {
		
			System.out.println("Received Request for: " + email);
			Employee emp = employeeService.getEmployee(email);
			if (emp != null) {
				if (request.getLatitude() != 0.0 && request.getLongitude() != 0.0) {
					emp.setLatitude(request.getLatitude());
					emp.setLongitude(request.getLongitude());
				}
				if (isNotNullOrEmpty(request.getPhoneNum())) {
					emp.setPhoneNum(request.getPhoneNum());
				}
				if (isNotNullOrEmpty(request.getDeviceId())
						&& request.getDeviceId().equals("DELETED")) {
					emp.setDeviceId(null);
				}
				employeeService.updateEmployee(emp);
				return new ResponseEntity<String>(HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}				
	}

	/**
	 * Return list of all citizens
	 * 
	 * @return citizen list
	 */
	@RequestMapping(value = "/citizen", method = RequestMethod.GET)
	public ResponseEntity<List<Citizen>> getAllCitizens() {
		LOGGER.info("Returning all citizens");
		return new ResponseEntity<List<Citizen>>(citizenService.getCitizens(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Long>> getStats() {
		HashMap<String, Long> reports = reportService.getReportStatistics();
		HashMap<String, Long> emps = employeeService.getEmployeesStatistics();

		HashMap<String, Long> stats = new HashMap<String, Long>();
		stats.putAll(reports);
		stats.putAll(emps);

		return new ResponseEntity<HashMap<String, Long>>(stats, HttpStatus.OK);
	}

	@RequestMapping("/ping")
	public ResponseEntity<String> getMessage() {
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/unassign", method = RequestMethod.GET)
	public ResponseEntity<String> unassignEmployee(
			@RequestParam(value = "id", required = true) int reportId) {
		Report report = reportService.getReport(reportId);
		report.setEmployee(null);
		reportService.updateReport(report);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	/**
	 * Retrieves a user after login
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/retrieve", method = RequestMethod.POST)
	public ResponseEntity<User> attemptLogin(
			@RequestParam(value = "email", required = true) String email) {
		User user = userService.getUser(email);
		if (user instanceof Employee) {
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}

	}

	@RequestMapping(value = "/password", method = RequestMethod.POST)
	public ResponseEntity<String> updatePassword(
			@RequestBody @Valid PasswordChangeRequest request) {
		CouncilAlertUser user = councilAlertUserService.getUser(request
				.getEmail());
		if (user != null) {
			if (new ShaPasswordEncoder(256).encodePassword(
					request.getPassword(), user.getSalt()).equals(
					user.getPassword())) {
				user.setPassword(new ShaPasswordEncoder(256).encodePassword(
						request.getNewPassword(), user.getSalt()));
				councilAlertUserService.updateUser(user);
				return new ResponseEntity<String>(HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						"Old Password does not match", HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<String>("User does not exist!",
					HttpStatus.BAD_REQUEST);
		}
	}
	
	private boolean isNotNullOrEmpty(String val) {
		return val != null && !val.isEmpty();
	}
}
