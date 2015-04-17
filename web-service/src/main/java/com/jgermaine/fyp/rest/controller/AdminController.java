package com.jgermaine.fyp.rest.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.exception.AssignmentException;
import com.jgermaine.fyp.rest.exception.ModelValidationException;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.EmployeeUpdateRequest;
import com.jgermaine.fyp.rest.model.Message;
import com.jgermaine.fyp.rest.model.PasswordChangeRequest;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;
import com.jgermaine.fyp.rest.task.TaskManager;
import com.jgermaine.fyp.rest.util.ResponseMessageUtil;

/**
 * 
 * @author JasonGermaine
 *
 *         This controller is restricted to being used by the employee web
 *         portal.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private static final Logger LOGGER = LogManager.getLogger(AdminController.class.getName());
	public static final String DELETED_DEVICE_ID = "DELETED";

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
	 * This request is used to retrieve an employee object. This event is most
	 * likely to occur after a successful login request.
	 * 
	 * There are 4 possible outputs
	 * <ul>
	 * <li>1. Employee exists for email - returns Employee + 200</li>
	 * <li>2. Email is for Citizen - returns 401</li>
	 * <li>3. No User exists for email - return 400</li>
	 * <li>4. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @param email
	 * @return Employee
	 */
	@RequestMapping(value = "/employee/{email:.+}", method = RequestMethod.GET)
	public ResponseEntity<User> attemptLogin(@PathVariable("email") String email) {
		try {
			Employee user = employeeService.getEmployee(email);
			return new ResponseEntity<User>(user, HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Creates a new Employee in the database. There are 4 possible outputs
	 * <ul>
	 * <li>1. Employee is valid and unique - returns 201</li>
	 * <li>2. Employee already exists in DB - returns 409</li>
	 * <li>3. Invalid Employee passed - returns 400</li>
	 * <li>4. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/employee", method = RequestMethod.POST)
	public ResponseEntity<Message> createEmployee(@RequestBody @Valid Employee employee, BindingResult result) {
		try {
			if (result.hasErrors())
				throw new ModelValidationException(result.getFieldErrorCount());
			
			employeeService.addEmployee(employee);
			councilAlertUserService.createNewUser(employee);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_EMPLOYEE_CREATION),
					HttpStatus.CREATED);
		} catch (PersistenceException | DataIntegrityViolationException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_EMPLOYEE_EXIST),
					HttpStatus.CONFLICT);
		} catch (ModelValidationException e) {
			return new ResponseEntity<Message>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes an Employee for a given email. There are 3 possible outputs
	 * <ul>
	 * <li>1. Employee exists for email. Employee is deleted - returns 200</li>
	 * <li>2. No User exists for email - return 400</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * *
	 * 
	 * @param email
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/employee/{email:.+}", method = RequestMethod.DELETE)
	public ResponseEntity<Message> removeEmployee(@PathVariable("email") String email) {
		try {
			Employee emp = employeeService.getEmployee(email);
			employeeService.removeEmployee(emp);
			if (emp.getReport() != null) {
				Report r = emp.getReport();
				r.setEmployee(null);
				reportService.updateReport(r);
			}
			councilAlertUserService.removeUser(emp);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_EMPLOYEE_REMOVAL), HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_EMPLOYEE_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates an Employee in the database for a given email. There are 4
	 * possible outputs
	 * <ul>
	 * <li>1. Employee exists and data is valid. - Employee updated + returns
	 * 200</li>
	 * <li>2. Invalid EmployeeUpdateRequest passed - returns 400</li>
	 * <li>3. No User exists for email - return 400</li>
	 * <li>4. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/employee/{email:.+}", method = RequestMethod.PUT)
	public ResponseEntity<Message> updateEmployee(@PathVariable("email") String email,
			@RequestBody @Valid EmployeeUpdateRequest request, BindingResult result) {
		try {
			if (result.hasErrors()) {
				throw new ModelValidationException(result.getFieldErrorCount());
			}
				
			Employee emp = employeeService.getEmployee(email);
			if (request.getLatitude() != 0.0 && request.getLongitude() != 0.0) {
				emp.setLatitude(request.getLatitude());
				emp.setLongitude(request.getLongitude());
			}
			if (isNotNullOrEmpty(request.getPhoneNum())) {
				emp.setPhoneNum(request.getPhoneNum());
			}
			if (isNotNullOrEmpty(request.getDeviceId()) && request.getDeviceId().equals(DELETED_DEVICE_ID)) {
				emp.setDeviceId(null);
			}
			employeeService.updateEmployee(emp);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_EMPLOYEE_UPDATE), HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_EMPLOYEE_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (ModelValidationException e) {
			return new ResponseEntity<Message>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Assigns a report with the given id to an Employee with the given email
	 * There are 3 possible outputs
	 * <ul>
	 * <li>1. Employee and Report exist - returns 200</li>
	 * <li>2. Employee or Report ids do not exist - returns 400</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping("/employee/assign")
	public ResponseEntity<Message> assignReportToEmployee(@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "id", required = true) int reportId) throws IOException {
		try {
			boolean reportSkip, empSkip;
			reportSkip = empSkip = false;
			Report report = reportService.getReport(reportId);

			// Check if report is already assigned to employee
			if (report.getEmployee() != null) {
				if (!report.getEmployee().getEmail().equals(email)) {
					throw new AssignmentException(AssignmentException.ExceptionType.REPORT);
				} else {
					reportSkip = true;
				}
			}

			// Check here for better performance if report check fails
			Employee employee = employeeService.getEmployee(email);

			// Check if employee is already assigned to report
			if (employee.getReport() != null) {
				// Check if employee is already assigned to another report
				if (employee.getReport().getId() != reportId) {
					throw new AssignmentException(AssignmentException.ExceptionType.EMPLOYEE);
				} else {
					empSkip = true;
				}
			}

			// Check if we need to update DB
			if (!reportSkip || !empSkip) {
				report.setEmployee(employee);
				reportService.updateReport(report);
			}

			// Send notification regardless if DB needs updating
			// New Thread spawned for GCM so it does no block response
			TaskManager.sendReportIdAsNotification(Integer.toString(reportId), report.getEmployee());
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_ASSIGNMENT), HttpStatus.OK);
		} catch (AssignmentException e) {
			return new ResponseEntity<Message>(new Message(e.getMessage() + ResponseMessageUtil.ERROR_ASSIGNMENT),
					HttpStatus.BAD_REQUEST);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_GENERIC_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates an Employee password in the database for a given email. There are
	 * 5 possible outputs
	 * <ul>
	 * <li>1. Employee exists and data is valid. - Employee updated + returns
	 * 200</li>
	 * <li>2. Invalid EmployeeUpdateRequest passed - returns 400</li>
	 * <li>3. No User exists for email - return 400</li>
	 * <li>4. Old password does not match database password - return 400</li>
	 * <li>5. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/employee/password/{email:.+}", method = RequestMethod.PUT)
	public ResponseEntity<Message> updatePassword(@PathVariable("email") String email,
			@RequestBody @Valid PasswordChangeRequest request, BindingResult result) {
		try {
			if (result.hasErrors())
				throw new ModelValidationException(result.getFieldErrorCount());

			CouncilAlertUser user = councilAlertUserService.getUser(email);
			if (user != null) {
				if (new ShaPasswordEncoder(256).encodePassword(request.getPassword(), user.getSalt()).equals(
						user.getPassword())) {
					user.setPassword(new ShaPasswordEncoder(256).encodePassword(request.getNewPassword(),
							user.getSalt()));
					councilAlertUserService.updateUser(user);
					return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_PASSWORD_UPDATE),
							HttpStatus.OK);
				} else {
					return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_PASSWORD_MISMATCH),
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (ModelValidationException e) {
			return new ResponseEntity<Message>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Unassigns a report with the given id. There are 3 possible outputs
	 * <ul>
	 * <li>1. Report exist - Update + returns 200</li>
	 * <li>2. Report does not exist - returns 400</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/report/unassign/{id}", method = RequestMethod.GET)
	public ResponseEntity<Message> unassignEmployee(@PathVariable("id") int reportId) {
		try {
			Report report = reportService.getReport(reportId);
			if (report.getEmployee() != null) {
				report.setEmployee(null);
				reportService.updateReport(report);
			}
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_REPORT_UPDATE), HttpStatus.OK);
		} catch (NoResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_REPORT_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Changes the status of a given report providing it exists
	 * 
	 * @param report
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/report/{id}/{status}", method = RequestMethod.PUT)
	public ResponseEntity<Message> reopenReport(@PathVariable("id") int id, @PathVariable("status") int statusInt) {
		try {
			if (statusInt != 0 && statusInt != 1)
				throw new IllegalArgumentException();

			Report report = reportService.getReport(id);
			boolean currentStatus = report.getStatus();
			boolean status = statusInt != 0;

			if (currentStatus != status) {
				if (status && report.getEmployee() != null) {
					report.setEmployee(null);
				}
				report.setStatus(status);
				reportService.updateReport(report);
			}
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_REPORT_UPDATE), HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_REPORT_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_INVALID_STATUS),
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves all citizens
	 * 
	 * @return List of Citizens
	 */
	@RequestMapping(value = "/citizen", method = RequestMethod.GET)
	public ResponseEntity<List<Citizen>> getAllCitizens() {
		try {
			return new ResponseEntity<List<Citizen>>(citizenService.getCitizens(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Citizen>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns statistics from the database.
	 * 
	 * @return statistic map
	 */
	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Long>> getStats() {
		try {
			HashMap<String, Long> reports = reportService.getReportStatistics();
			HashMap<String, Long> emps = employeeService.getEmployeesStatistics();

			HashMap<String, Long> stats = new HashMap<String, Long>();
			stats.putAll(reports);
			stats.putAll(emps);

			return new ResponseEntity<HashMap<String, Long>>(stats, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<HashMap<String, Long>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Determines whether a string is not null and not empty
	 * 
	 * @param value
	 * @return isValid
	 */
	private boolean isNotNullOrEmpty(String value) {
		return value != null && !value.isEmpty();
	}
}
