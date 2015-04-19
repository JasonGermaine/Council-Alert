package com.jgermaine.fyp.rest.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.Arrays;

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
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.util.ResponseMessageUtil;
import com.jgermaine.fyp.rest.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebApplication.class)
public class CitizenControllerTest {

	@Autowired
	private WebApplicationContext context;

	@InjectMocks
	CitizenController controller;

	@Mock
	private CouncilAlertUserDetailsService councilAlertUserService;

	@Mock
	private CitizenServiceImpl citizenService;

	private MockMvc mvc;
	private Citizen citizen;
	private Report report;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		citizen = TestUtil.getDefaultCitizen();
		report = TestUtil.getDefaultReport();
	}

	@Test
	public void testAddCitizenSuccess() throws Exception {

		Mockito.doNothing().when(councilAlertUserService).createNewUser(Mockito.any(User.class));

		// @formatter:off
		mvc.perform(
				post("/api/citizen/").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(citizen)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_USER_CREATION)));
		// @formatter:on
	}

	@Test
	public void testAddInvalidCitizenFailure() throws Exception {
		Citizen citz = TestUtil.getDefaultCitizen();
		citz.setEmail("1234-Invalid");
		
		// @formatter:off
		mvc.perform(
				post("/api/citizen/").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(citz)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}

	@Test
	public void testNonUniqueCitizenFailure() throws Exception {

		Mockito.doThrow(DataIntegrityViolationException.class).when(councilAlertUserService)
				.createNewUser(Mockito.any(Citizen.class));

		// @formatter:off
		mvc.perform(
				post("/api/citizen/").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(citizen)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_USER_EXIST)));
		// @formatter:on
	}
	
	@Test
	public void testGetReportForCitizenSuccess() throws Exception {		
		when(citizenService.getReportsForCitizen(Mockito.anyString())).thenReturn(Arrays.asList(report));
		
		// @formatter:off
		mvc.perform(
				get("/api/citizen/report/sample@email.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(report.getId())));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetReportForCitizenFailure() throws Exception {		
		when(citizenService.getReportsForCitizen(Mockito.anyString())).thenThrow(NoResultException.class);
		// @formatter:off
		mvc.perform(
				get("/api/citizen/report/sample@email.com"))
				.andExpect(status().isBadRequest());
		// @formatter:on
	}

}
