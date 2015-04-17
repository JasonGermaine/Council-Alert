package com.jgermaine.fyp.rest.controller;

import java.io.IOException;
import java.nio.charset.Charset;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;
import com.jgermaine.fyp.rest.config.DatabaseInitializer;
import com.jgermaine.fyp.rest.config.Secret;
import com.jgermaine.fyp.rest.config.WebApplication;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.EmployeeUpdateRequest;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;
import com.jgermaine.fyp.rest.util.ResponseMessageUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebApplication.class)
public class AdminControllerTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FilterChainProxy securityFilterChain;

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
	private EmployeeUpdateRequest request;
	private Citizen citizen;
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		employee = getDefaultEmp();
		request = getUpdateRequest();
		citizen = getDefaultCitizen();
	}

	@Test
	public void testRetrieveEmployeeExists() throws Exception {
		when(employeeService.getEmployee(anyString())).thenReturn(employee);
		
		// @formatter:off
		mvc.perform(
				get("/api/admin/employee/sample@email.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is(employee.getEmail())));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRetrieveEmployeeNotExists() throws Exception {
		when(employeeService.getEmployee(anyString())).thenThrow(NoResultException.class);
		
		// @formatter:off
		mvc.perform(
				get("/api/admin/employee/sample@email.com"))
					.andExpect(status().isBadRequest());
		// @formatter:on
	}

	@Test
	public void testAddEmployeeSuccess() throws Exception {
		
		Mockito.doNothing().when(employeeService).addEmployee(Mockito.any(Employee.class));
		Mockito.doNothing().when(councilAlertUserService).createNewUser(Mockito.any(User.class));
		
		// @formatter:off
		mvc.perform(
				post("/api/admin/employee")
					.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), 
							MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
					.content(convertObjectToJsonBytes(employee)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_EMPLOYEE_CREATION)));
		// @formatter:on
	}
	
	@Test
	public void testAddInvalidEmployeeFailure() throws Exception {
		Employee emp = getDefaultEmp();
		emp.setFirstName("1234-Invalid");
		// @formatter:off
		mvc.perform(
				post("/api/admin/employee")
					.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), 
							MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
					.content(convertObjectToJsonBytes(emp)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}
	
	@Test
	public void testNonUniqueEmployeeFailure() throws Exception {
		
		Employee emp = getDefaultEmp();
		Mockito.doThrow(DataIntegrityViolationException.class).when(employeeService).addEmployee(Mockito.any(Employee.class));
		
		// @formatter:off
		mvc.perform(
				post("/api/admin/employee")
					.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), 
							MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
					.content(convertObjectToJsonBytes(emp)))
					.andExpect(status().isConflict())
					.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_EMPLOYEE_EXIST)));
		// @formatter:on
	}

	
	@Test
	public void testDeleteEmployeeSuccess() throws Exception {
		
		when(employeeService.getEmployee(anyString())).thenReturn(employee);
		Mockito.doNothing().when(employeeService).removeEmployee(Mockito.any(Employee.class));
		Mockito.doNothing().when(councilAlertUserService).removeUser(Mockito.any(User.class));
		
		// @formatter:off
		mvc.perform(
				delete("/api/admin/employee/sample@email.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_EMPLOYEE_REMOVAL)));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteEmployeeFailure() throws Exception {
		
		when(employeeService.getEmployee(anyString())).thenThrow(NoResultException.class);
		
		// @formatter:off
		mvc.perform(
				delete("/api/admin/employee/sample@email.com"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_EMPLOYEE_NOT_EXIST)));
		// @formatter:on
	}

	@Test
	public void testUpdateEmployeeSuccess() throws Exception {
		when(employeeService.getEmployee(anyString())).thenReturn(employee);
		Mockito.doNothing().when(employeeService).updateEmployee(Mockito.any(Employee.class));
		
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/sample@email.com")
					.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), 
							MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
					.content(convertObjectToJsonBytes(request)))
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
				put("/api/admin/employee/sample@email.com")
					.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), 
							MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
					.content(convertObjectToJsonBytes(request)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_EMPLOYEE_NOT_EXIST)));
		// @formatter:on
	}

	@Test
	public void testUpdateInvalidEmployeeFailure() throws Exception {
		request.setPhoneNum("Number @!$%");
		// @formatter:off
		mvc.perform(
				put("/api/admin/employee/sample@email.com")
					.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), 
							MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")))
					.content(convertObjectToJsonBytes(request)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}
	
	private Employee getDefaultEmp() {
		Employee employee = new Employee();
		employee.setEmail(DatabaseInitializer.INIT_USER);
		employee.setPassword(Secret.SECRET_PASSWORD);
		employee.setFirstName("Jason");
		employee.setLastName("Germaine");
		employee.setPhoneNum("12334567");
		employee.setLatitude(53.2884615);
		employee.setLongitude(-6.3525261);
		return employee;
	}

	private EmployeeUpdateRequest getUpdateRequest() {
		EmployeeUpdateRequest request = new EmployeeUpdateRequest();
		request.setPhoneNum("12334567");
		request.setDeviceId(AdminController.DELETED_DEVICE_ID);
		request.setLatitude(53.2884615);
		request.setLongitude(-6.3525261);		
		return request;
	}
	
	private Citizen getDefaultCitizen() {
		Citizen citz = new Citizen();
		citz.setEmail(DatabaseInitializer.INIT_USER);
		citz.setPassword(Secret.SECRET_PASSWORD);
		return citz;
	}
	
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
