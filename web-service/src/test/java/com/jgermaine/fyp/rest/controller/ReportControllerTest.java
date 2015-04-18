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
import com.jgermaine.fyp.rest.model.Entry;
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
public class ReportControllerTest {

	@Autowired
	private WebApplicationContext context;

	@InjectMocks
	ReportController controller;

	@Mock
	private CouncilAlertUserDetailsService councilAlertUserService;

	@Mock
	private ReportServiceImpl reportService;

	@Mock
	private CitizenServiceImpl citizenService;

	private MockMvc mvc;
	private Employee employee;
	private Citizen citizen;
	private Report report;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		employee = TestUtil.getDefaultEmp();
		report = TestUtil.getDefaultReport();
		citizen = TestUtil.getDefaultCitizen();
	}

	@Test
	public void testGetAllReportsSuccess() throws Exception {

		when(reportService.getReports()).thenReturn(Arrays.asList(report));

		// @formatter:off
		mvc.perform(get("/api/report/")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is(report.getId())));
		// @formatter:on
	}

	@Test
	public void testGetTodayReportsSuccess() throws Exception {
		when(reportService.getTodayReports()).thenReturn(Arrays.asList(report));

		// @formatter:off
		mvc.perform(get("/api/report/today")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(report.getId())));
		// @formatter:on
	}

	@Test
	public void testGetCompleteReportsSuccess() throws Exception {
		when(reportService.getCompleteReports()).thenReturn(Arrays.asList(report));

		// @formatter:off
		mvc.perform(get("/api/report/complete")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(report.getId())));
		// @formatter:on
	}

	@Test
	public void testGetIncompleteReportsSuccess() throws Exception {
		when(reportService.getIncompleteReports()).thenReturn(Arrays.asList(report));

		// @formatter:off
		mvc.perform(get("/api/report/incomplete")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(report.getId())));
		// @formatter:on
	}

	@Test
	public void testGetNearReportsSuccess() throws Exception {

		when(reportService.getUnassignedNearReports(anyDouble(), anyDouble())).thenReturn(Arrays.asList(report));

		// @formatter:off
		mvc.perform(get("/api/report/open?lat=" + report.getLatitude() + "&lon=" + report.getLongitude()))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].id", is(report.getId())));
		// @formatter:on
	}

	@Test
	public void testGetReportForIdSuccess() throws Exception {

		when(reportService.getReport(anyInt())).thenReturn(report);

		// @formatter:off
		mvc.perform(get("/api/report/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(report.getId())));
		// @formatter:on
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetReportForIdFailure() throws Exception {

		when(reportService.getReport(anyInt())).thenThrow(NoResultException.class);

		// @formatter:off
		mvc.perform(get("/api/report/1")).andExpect(status().isBadRequest());
		// @formatter:on
	}

	@Test
	public void testAddReportSuccess() throws Exception {

		when(citizenService.getCitizen(anyString())).thenReturn(citizen);
		Mockito.doNothing().when(reportService).addReport(Mockito.any(Report.class));

		// @formatter:off
		mvc.perform(
				post("/api/report/citizen/" + citizen.getEmail())
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8")))
				.content(TestUtil.convertObjectToJsonBytes(report)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_REPORT_CREATION)));
		// @formatter:on
	}
	
	@Test
	public void testAddInvalidReportFailure() throws Exception {

		Report rpt = TestUtil.getDefaultReport();
		rpt.setName("$Invalid$");
		// @formatter:off
		mvc.perform(
				post("/api/report/citizen/" + citizen.getEmail())
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8")))
				.content(TestUtil.convertObjectToJsonBytes(rpt)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}
	
	@Test
	public void testAddNonUniqueReportFailure() throws Exception {

		when(citizenService.getCitizen(anyString())).thenReturn(citizen);
		Mockito.doThrow(DataIntegrityViolationException.class).when(reportService).addReport(Mockito.any(Report.class));

		// @formatter:off
		mvc.perform(
				post("/api/report/citizen/" + citizen.getEmail())
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8")))
				.content(TestUtil.convertObjectToJsonBytes(report)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_REPORT_EXIST)));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAddReportCitizenExistFailure() throws Exception {

		when(citizenService.getCitizen(anyString())).thenThrow(NoResultException.class);		

		// @formatter:off
		mvc.perform(
				post("/api/report/citizen/" + citizen.getEmail())
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8")))
				.content(TestUtil.convertObjectToJsonBytes(report)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_USER_NOT_EXIST)));
		// @formatter:on
	}
	
	@Test
	public void testCloseReportSuccess() throws Exception {

		when(citizenService.getCitizen(anyString())).thenReturn(citizen);
		Mockito.doNothing().when(reportService).addReport(Mockito.any(Report.class));

		// @formatter:off
		mvc.perform(
				post("/api/report/close")
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8")))
				.content(TestUtil.convertObjectToJsonBytes(report)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_REPORT_UPDATE)));
		// @formatter:on
	}
	
	@Test
	public void testCloseInvalidReportFailure() throws Exception {

		Report rpt = TestUtil.getDefaultReport();
		rpt.setName("$Invalid$");
		// @formatter:off
		mvc.perform(
				post("/api/report/close")
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8")))
				.content(TestUtil.convertObjectToJsonBytes(rpt)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCloseReportCitizenExistFailure() throws Exception {

		when(citizenService.getCitizen(anyString())).thenThrow(NoResultException.class);		

		// @formatter:off
		mvc.perform(
				post("/api/report/close")
				.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8")))
				.content(TestUtil.convertObjectToJsonBytes(report)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_USER_NOT_EXIST)));
		// @formatter:on
	}
	
	@Test
	public void testGetReportForEmployeeSuccess() throws Exception {

		when(reportService.getReportForEmp(anyString())).thenReturn(report);

		// @formatter:off
		mvc.perform(get("/api/report/employee/sample@email.com")).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(report.getId())));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetReportForEmployeeFailure() throws Exception {

		when(reportService.getReportForEmp(anyString())).thenThrow(NoResultException.class);

		// @formatter:off
		mvc.perform(get("/api/report/employee/sample@email.com")).andExpect(status().isBadRequest());
		// @formatter:on
	}
	
	@Test
	public void testUpdateReportSuccess() throws Exception {		
		when(reportService.getReport(anyInt())).thenReturn(report);
		Mockito.doNothing().when(reportService).updateReport(Mockito.any(Report.class));
		Entry entry = new Entry();
		entry.setAuthor("author@email.com");
		entry.setComment("comment");
		report.addEntry(entry);
		// @formatter:off
		mvc.perform(
				put("/api/report/1").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(report)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.SUCCESS_REPORT_UPDATE)));
		// @formatter:on
	}
	
	@Test
	public void testUpdateInvalidReportFailure() throws Exception {		
		Report rpt = TestUtil.getDefaultReport();
		Entry entry = new Entry();
		entry.setAuthor("authoremail.com");
		entry.setComment("comment");
		rpt.addEntry(entry);
		// @formatter:off
		mvc.perform(
				put("/api/report/1").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(rpt)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("1 " + ResponseMessageUtil.ERROR_INVALID_DATA)));
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateNonExistentReportFailure() throws Exception {		
		when(reportService.getReport(anyInt())).thenThrow(NoResultException.class);
		Entry entry = new Entry();
		entry.setAuthor("author@email.com");
		entry.setComment("comment");
		
		// @formatter:off
		mvc.perform(
				put("/api/report/1").contentType(
						new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(),
								Charset.forName("utf8"))).content(TestUtil.convertObjectToJsonBytes(report)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is(ResponseMessageUtil.ERROR_REPORT_NOT_EXIST)));
		// @formatter:on
	}
}
