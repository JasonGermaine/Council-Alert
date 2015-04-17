package com.jgermaine.fyp.rest.security;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.persistence.NoResultException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;
import com.jgermaine.fyp.rest.config.DatabaseInitializer;
import com.jgermaine.fyp.rest.config.Secret;
import com.jgermaine.fyp.rest.config.WebApplication;
import com.jgermaine.fyp.rest.controller.AdminController;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebApplication.class)
public class OAuthSecurityTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FilterChainProxy securityFilterChain;

	@InjectMocks
	AdminController controller;

	private MockMvc mvc;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.addFilter(securityFilterChain)
				.build();
	}
	
	@Test
	public void testUnauthorizedUser() throws Exception {
		// @formatter:off
		mvc.perform(
				get("/api/admin/employee/" + DatabaseInitializer.INIT_USER)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
		// @formatter:on
	}

	@Test
	public void testAuthorizedUser() throws Exception {
		String accessToken = getAccessToken(DatabaseInitializer.INIT_USER, Secret.SECRET_PASSWORD);
		
		// @formatter:off
		mvc.perform(
				get("/api/admin/employee/" + DatabaseInitializer.INIT_USER)
					.header("Authorization", "Bearer " + accessToken))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.email", is(DatabaseInitializer.INIT_USER)));
		// @formatter:on
	}

	private String getAccessToken(String username, String password) throws Exception {
		String authorization = "Basic " + new String(
				Base64Utils.encode("angular-client:council-alert-angular-secret".getBytes()));
		
		// @formatter:off
		String content = mvc
				.perform(
						post("/oauth/token")
								.header("Authorization", authorization)
								.contentType(MediaType.APPLICATION_FORM_URLENCODED)
								.param("username", username)
								.param("password", password)
								.param("grant_type", "password")
								.param("scope", "read write trust")
								.param("client_id", "angular-client")
								.param("client_secret", "council-alert-angular-secret"))																
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.access_token", is(notNullValue())))				
				.andReturn().getResponse().getContentAsString();
		// @formatter:on

		return content.substring(17, 53);
	}

}
