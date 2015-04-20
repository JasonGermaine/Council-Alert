package com.jgermaine.fyp.rest.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;

import org.hibernate.validator.constraints.NotEmpty;
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
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;
import com.jgermaine.fyp.rest.config.DatabaseInitializer;
import com.jgermaine.fyp.rest.config.Secret;
import com.jgermaine.fyp.rest.config.WebApplication;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.EmployeeUpdateRequest;
import com.jgermaine.fyp.rest.model.PasswordChangeRequest;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.Role;
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
public class EmployeeControllerTest {

	@Autowired
	private WebApplicationContext context;

	@InjectMocks
	EmployeeController controller;

	@Mock
	private CouncilAlertUserDetailsService councilAlertUserService;

	@Mock
	private EmployeeServiceImpl employeeService;

	private MockMvc mvc;
	private Employee employee;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		employee = TestUtil.getDefaultEmp();
	}

	@Test
	public void testGetAllEmployeesSuccess() throws Exception {

		when(employeeService.getEmployees(anyInt())).thenReturn(Arrays.asList(employee));

		// @formatter:off
		mvc.perform(get("/api/employee/")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is(employee.getEmail())));
		// @formatter:on
	}
	
	@Test
	public void testGetAssignedEmployeesSuccess() throws Exception {

		when(employeeService.getAssignedEmployees(anyInt())).thenReturn(Arrays.asList(employee));

		// @formatter:off
		mvc.perform(get("/api/employee/assigned")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is(employee.getEmail())));
		// @formatter:on
	}
	
	@Test
	public void testGetUnassignedEmployeesSuccess() throws Exception {

		when(employeeService.getUnassignedEmployees(anyInt())).thenReturn(Arrays.asList(employee));

		// @formatter:off
		mvc.perform(get("/api/employee/unassigned")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is(employee.getEmail())));
		// @formatter:on
	}
	
	@Test
	public void testGetNearEmployeesSuccess() throws Exception {

		when(employeeService.getUnassignedNearEmployees(anyDouble(), anyDouble())).thenReturn(Arrays.asList(employee));

		// @formatter:off
		mvc.perform(get("/api/employee/open?lat=" + employee.getLatitude() 
				+ "&lon=" + employee.getLongitude())).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is(employee.getEmail())));
		// @formatter:on
	}
	
	@Test
	public void testGetEmployeesByEmailSuccess() throws Exception {

		when(employeeService.getEmployee(anyString())).thenReturn(employee);

		// @formatter:off
		mvc.perform(get("/api/employee/sample@email.com")).andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is(employee.getEmail())));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetEmployeesByEmailFailure() throws Exception {

		when(employeeService.getEmployee(anyString())).thenThrow(NoResultException.class);

		// @formatter:off
		mvc.perform(get("/api/employee/sample@email.com")).andExpect(status().isBadRequest());
		// @formatter:on
	}
}
