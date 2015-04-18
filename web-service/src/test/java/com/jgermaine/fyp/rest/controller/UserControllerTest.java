package com.jgermaine.fyp.rest.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jgermaine.fyp.rest.config.WebApplication;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.UserRequest;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;
import com.jgermaine.fyp.rest.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebApplication.class)
public class UserControllerTest {

	@Autowired
	private WebApplicationContext context;

	@InjectMocks
	UserController controller;

	@Mock
	private EmployeeServiceImpl employeeService;

	@Mock
	private UserServiceImpl userService;

	private MockMvc mvc;
	private Employee employee;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		employee = TestUtil.getDefaultEmp();
	}

	@Test
	public void testGetUserSuccess() throws Exception {

		UserRequest request = new UserRequest();
		request.setEmail("sample@email.com");
		request.setDeviceId("deviceId");

		when(userService.getUser(Mockito.anyString())).thenReturn(employee);
		Mockito.doNothing().when(employeeService).updateEmployee(Mockito.any(Employee.class));
		
		// @formatter:off
		mvc.perform(
				post("/api/user/").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is(employee.getEmail())))
				.andExpect(jsonPath("$.deviceId", is(request.getDeviceId())));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetUserFailure() throws Exception {

		UserRequest request = new UserRequest();
		request.setEmail("sample@email.com");
		request.setDeviceId("deviceId");

		when(userService.getUser(Mockito.anyString())).thenThrow(NoResultException.class);
	
		
		// @formatter:off
		mvc.perform(
				post("/api/user/").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(request)))
				.andExpect(status().isBadRequest());
		// @formatter:on
	}
}
