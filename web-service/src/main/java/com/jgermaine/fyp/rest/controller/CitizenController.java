package com.jgermaine.fyp.rest.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;

/**
 * Handles all RESTful operations for citizens
 * @author jason
 */
@RestController
@RequestMapping("/api/citizen")
public class CitizenController {

	private static final Logger LOGGER = LogManager
			.getLogger(CitizenController.class.getName());

	@Autowired
	private CitizenServiceImpl citizenService;
	
	/**
	 * Return list of all citizens
	 * @return citizen list
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ResponseEntity<List<Citizen>> getAllCitizens() {
		LOGGER.info("Returning all citizens");
		return new ResponseEntity<List<Citizen>>(citizenService.getCitizens(), HttpStatus.OK);
	}
	
	/**
	 * Creates a new Citizen
	 * There are 3 possible outputs
	 * <ul>
	 * <li>1. Citizen is valid and unique - returns 201</li>
	 * <li>2. Citizen already exists in DB - returns 409</li>
	 * <li>3. Invalid Citizen passed - returns 400</li>
	 * </ul>
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<String> createCitizen(@RequestBody @Valid Citizen citizen) {
		if (citizenService.getCitizen(citizen.getEmail()) == null) {			
			citizenService.addCitizen(citizen);
			LOGGER.info("Creating Citizen: " + citizen.getEmail());
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} else {
			LOGGER.warn("Employee already exists " + citizen.getEmail());
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		}
	}	
	
	/**
	 * Return list of reports for a given citizen
	 * @return report list
	 */
	@RequestMapping(value="/report", method=RequestMethod.GET)
	public ResponseEntity<List<Report>> getCitizenReports(@RequestParam(value = "email", required = true) String email) {
		Citizen citz = citizenService.getCitizen(email);
		if (citz != null) {
			LOGGER.info("Returning reports for: " + email);
			return new ResponseEntity<List<Report>>(citz.getReports(), HttpStatus.OK);
		} else {
			LOGGER.info("Citizen does not exist: " + email);
			return new ResponseEntity<List<Report>>(HttpStatus.BAD_REQUEST);
		}
	}
}