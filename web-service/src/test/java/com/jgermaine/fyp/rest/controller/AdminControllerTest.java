package com.jgermaine.fyp.rest.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jgermaine.fyp.rest.config.WebApplication;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.EmployeeUpdateRequest;
import com.jgermaine.fyp.rest.model.PasswordChangeRequest;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;
import com.jgermaine.fyp.rest.util.ResponseMessageUtil;
import com.jgermaine.fyp.rest.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebApplication.class)
public class AdminControllerTest {

	@Autowired
	private WebApplicationContext context;

	@InjectMocks
	AdminController controller;

	@Mock
	private CouncilAlertUserDetailsService councilAlertUserService;

	@Mock
	private EmployeeServiceImpl employeeService;

	@Mock
	private ReportServiceImpl reportService;

	@Mock
	private UserServiceImpl userService;

	@Mock
	private CitizenServiceImpl citizenService;

	private MockMvc mvc;
	private Employee employee;
	private EmployeeUpdateRequest updateRequest;
	private Report report;
	private Citizen citizen;
	private PasswordChangeRequest passwordRequest;
	private CouncilAlertUser user;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		employee = TestUtil.getDefaultEmp();
		updateRequest = TestUtil.getUpdateRequest();
		citizen = TestUtil.getDefaultCitizen();
		report = TestUtil.getDefaultReport();
		user = TestUtil.getDefaultCouncilAlertUser();
		passwordRequest = TestUtil.getPasswordRequest();
	}

	@Test
	public void testRetrieveEmployeeExists() throws Exception {
		when(employeeService.getEmployee(anyString())).thenReturn(employee);

		// @formatter:off
		mvc.perform(get("/api/admin/employee/sample@email.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is(employee.getEmail())));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRetrieveEmployeeNotExists() throws Exception {
		when(employeeService.getEmployee(anyString())).thenThrow(NoResultException.class);

		// @formatter:off
		mvc.perform(get("/api/admin/employee/sample@email.com")).andExpect(status().isBadRequest());
		// @formatter:on
	}

	@Test
	public void testAddEmployeeSuccess() throws Exception {

		Mockito.doNothing().when(councilAlertUserService).createNewUser(Mockito.any(User.class));

		// @formatter:off
		mvc.perform(
				post("/api/admin/employee").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(employee)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_EMPLOYEE_CREATION)));
		// @formatter:on
	}

	@Test
	public void testAddInvalidEmployeeFailure() throws Exception {
		Employee emp = TestUtil.getDefaultEmp();
		emp.setFirstName("1234-Invalid");
		// @formatter:off
		mvc.perform(
				post("/api/admin/employee").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(emp)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}

	@Test
	public void testNonUniqueEmployeeFailure() throws Exception {

		Employee emp = TestUtil.getDefaultEmp();
		Mockito.doThrow(DataIntegrityViolationException.class).when(councilAlertUserService)
				.createNewUser(Mockito.any(Employee.class));

		// @formatter:off
		mvc.perform(
				post("/api/admin/employee").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(emp)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_EMPLOYEE_EXIST)));
		// @formatter:on
	}

	@Test
	public void testDeleteEmployeeSuccess() throws Exception {
		Employee emp = TestUtil.getDefaultEmp();
		emp.setReport(TestUtil.getDefaultReport());
		when(employeeService.getEmployee(anyString())).thenReturn(emp);
		Mockito.doNothing().when(employeeService).removeEmployee(Mockito.any(Employee.class));
		Mockito.doNothing().when(councilAlertUserService).removeUser(Mockito.any(User.class));

		// @formatter:off
		mvc.perform(delete("/api/admin/employee/sample@email.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_EMPLOYEE_REMOVAL)));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteEmployeeFailure() throws Exception {

		when(employeeService.getEmployee(anyString())).thenThrow(NoResultException.class);

		// @formatter:off
		mvc.perform(delete("/api/admin/employee/sample@email.com")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_EMPLOYEE_NOT_EXIST)));
		// @formatter:on
	}

	@Test
	public void testUpdateEmployeeSuccess() throws Exception {		
		when(employeeService.getEmployee(anyString())).thenReturn(employee);
		Mockito.doNothing().when(employeeService).updateEmployee(Mockito.any(Employee.class));
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/sample@email.com").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_EMPLOYEE_UPDATE)));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateEmployeeNotExistFailure() throws Exception {
		when(employeeService.getEmployee(anyString())).thenThrow(NoResultException.class);

		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/sample@email.com").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(updateRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_EMPLOYEE_NOT_EXIST)));
		// @formatter:on
	}

	@Test
	public void testUpdateInvalidEmployeeFailure() throws Exception {
		updateRequest.setPhoneNum("Number @!$%");
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/sample@email.com").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(updateRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}

	@Test
	public void testAssignmentSuccess() throws Exception {
		// @formatter:off

		when(employeeService.getEmployee(anyString())).thenReturn(employee);
		when(reportService.getReport(anyInt())).thenReturn(report);
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		Mockito.doNothing().when(employeeService).sendEmployeeNotification(Mockito.any(Employee.class), anyInt());
		
		mvc.perform(
				get("/api/admin/employee//assign?email=sample@email.com&id=1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_ASSIGNMENT)));
		// @formatter:on
	}

	@Test
	public void testAssignmentClosedReportFailure() throws Exception {
		// @formatter:off
		Report rpt = TestUtil.getDefaultReport();
		rpt.setStatus(true);
		when(reportService.getReport(anyInt())).thenReturn(rpt);		
		mvc.perform(
				get("/api/admin/employee//assign?email=sample@email.com&id=1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_CLOSED_ASSIGNMENT)));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAssignmentReportNotExist() throws Exception {
		// @formatter:off
		when(reportService.getReport(anyInt())).thenThrow(NoResultException.class);
		
		mvc.perform(
				get("/api/admin/employee//assign?email=sample@email.com&id=1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_GENERIC_NOT_EXIST)));
		// @formatter:on
	}
	
	@Test
	public void testAssignmentAssignedEmployee() throws Exception {
		// @formatter:off

		Employee emp = TestUtil.getDefaultEmp();
		Report rpt = TestUtil.getDefaultReport();
		rpt.setId(2);
		emp.setReport(rpt);
		when(employeeService.getEmployee(anyString())).thenReturn(emp);
		when(reportService.getReport(anyInt())).thenReturn(report);
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		
		mvc.perform(
				get("/api/admin/employee//assign?email=sample@email.com&id=1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Employee" + ResponseMessageUtil.ERROR_ASSIGNMENT)));
		// @formatter:on
	}

	@Test
	public void testAssignmentAssignedReport() throws Exception {
		// @formatter:off

		Employee emp = TestUtil.getDefaultEmp();
		Report rpt = TestUtil.getDefaultReport();
		emp.setEmail("anothersample@email.com");
		rpt.setEmployee(emp);
		when(employeeService.getEmployee(anyString())).thenReturn(employee);
		when(reportService.getReport(anyInt())).thenReturn(rpt);
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		mvc.perform(
				get("/api/admin/employee//assign?email=sample@email.com&id=1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Report" + ResponseMessageUtil.ERROR_ASSIGNMENT)));
		// @formatter:on
	}
	
	@Test
	public void testAssignmentAlreadyAssigned() throws Exception {
		// @formatter:off

		employee.setReport(report);
		report.setEmployee(employee);
		when(employeeService.getEmployee(anyString())).thenReturn(employee);
		when(reportService.getReport(anyInt())).thenReturn(report);
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		Mockito.doNothing().when(employeeService).sendEmployeeNotification(Mockito.any(Employee.class), anyInt());
		
		mvc.perform(
				get("/api/admin/employee/assign?email="
						+ employee.getEmail() + "&id=" + report.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_ASSIGNMENT)));
		// @formatter:on
	}

	@Test
	public void testChangePasswordSuccess() throws Exception {		
		when(councilAlertUserService.getUser(anyString())).thenReturn(user);
		Mockito.doNothing().when(councilAlertUserService).updateUser(Mockito.any(CouncilAlertUser.class));
		
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/password/sample@email.com").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(passwordRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_PASSWORD_UPDATE)));
		// @formatter:on
	}
	
	@Test
	public void testChangePasswordMismatchFailure() throws Exception {		
		when(councilAlertUserService.getUser(anyString())).thenReturn(user);
		PasswordChangeRequest invalidRequest = TestUtil.getPasswordRequest();
		invalidRequest.setPassword("notthesamepassword");
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/password/sample@email.com").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(invalidRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_PASSWORD_MISMATCH)));
		// @formatter:on
	}
	
	@Test
	public void testChangePasswordInvalidPasswordFailure() throws Exception {		
		when(councilAlertUserService.getUser(anyString())).thenReturn(user);
		Mockito.doNothing().when(councilAlertUserService).updateUser(Mockito.any(CouncilAlertUser.class));
		PasswordChangeRequest invalidRequest = TestUtil.getPasswordRequest();
		invalidRequest.setNewPassword("IllegalPassword!()%^&@~#{}[]");
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/password/sample@email.com").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(invalidRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChangePasswordUserExistFailure() throws Exception {		
		when(councilAlertUserService.getUser(anyString())).thenThrow(NoResultException.class);
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/password/sample@email.com").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(passwordRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_EMPLOYEE_NOT_EXIST)));
		// @formatter:on
	}

	@Test
	public void testUnassignmentSuccess() throws Exception {
		// @formatter:off
		report.setEmployee(employee);
		when(reportService.getReport(anyInt())).thenReturn(report);
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		
		mvc.perform(
				get("/api/admin/report/unassign/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_REPORT_UPDATE)));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnassignmentReportExistFailure() throws Exception {
		// @formatter:off
		
		when(reportService.getReport(anyInt())).thenThrow(NoResultException.class);
		
		mvc.perform(
				get("/api/admin/report/unassign/1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_REPORT_NOT_EXIST)));
		// @formatter:on
	}

	@Test
	public void testChangeReportStatusSuccess() throws Exception {
		report.setEmployee(employee);
		when(reportService.getReport(anyInt())).thenReturn(report);
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		
		// @formatter:off
		mvc.perform(
				put("/api/admin/report/1/1").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_REPORT_UPDATE)));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChangeReportStatusReportExistFailure() throws Exception {
		when(reportService.getReport(anyInt())).thenThrow(NoResultException.class);
		
		// @formatter:off
		mvc.perform(
				put("/api/admin/report/1/1").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_REPORT_NOT_EXIST)));
		// @formatter:on
	}
	
	@Test
	public void testChangeReportStatusInvalidStatusFailure() throws Exception {		
		// @formatter:off
		mvc.perform(
				put("/api/admin/report/1/666").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_INVALID_STATUS)));
		// @formatter:on
	}

	
	@Test
	public void testGetCitizensSuccess() throws Exception {		
		when(citizenService.getCitizens()).thenReturn(Arrays.asList(citizen));
		// @formatter:off
		mvc.perform(
				get("/api/admin/citizen"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is(citizen.getEmail())));
		// @formatter:on
	}
	
	@Test
	public void testGetStatisticsSuccess() throws Exception {		
		HashMap<String, Long> empMap = new HashMap<String, Long>();
		HashMap<String, Long> reportMap = new HashMap<String, Long>();
		empMap.put("emp", (long) 1);
		reportMap.put("report", (long) 2);
		
		when(employeeService.getEmployeesStatistics()).thenReturn(empMap);
		when(reportService.getReportStatistics()).thenReturn(reportMap);
		
		// @formatter:off
		mvc.perform(
				get("/api/admin/stats"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.emp", is((int) (long) empMap.get("emp"))))
				.andExpect(jsonPath("$.report", is((int) (long) reportMap.get("report"))));
		// @formatter:on
	}
}
