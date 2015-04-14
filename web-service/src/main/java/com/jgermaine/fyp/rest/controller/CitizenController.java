package com.jgermaine.fyp.rest.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.UserServiceImpl;
import com.jgermaine.fyp.rest.util.ResponseMessageUtil;

/**
 * 
 * @author JasonGermaine
 * 
 *         Handles requests for Citizens
 *
 */
@RestController
@RequestMapping("/api/citizen")
public class CitizenController {

	private static final Logger LOGGER = LogManager.getLogger(CitizenController.class.getName());

	@Autowired
	private CouncilAlertUserDetailsService councilAlertUserService;

	@Autowired
	private CitizenServiceImpl citizenService;

	@Autowired
	private UserServiceImpl userService;

	/**
	 * Creates a new Citizen. There are 4 possible outputs
	 * <ul>
	 * <li>1. Citizen is valid and unique - returns 201</li>
	 * <li>2. Citizen already exists in DB - returns 409</li>
	 * <li>3. Invalid Citizen passed - returns 400</li>
	 * <li>4. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> createCitizen(@RequestBody @Valid Citizen citizen) {
		try {
			citizenService.addCitizen(citizen);
			councilAlertUserService.createNewUser(citizen);
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} catch (PersistenceException | DataIntegrityViolationException e) {
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves reports for a Citizen for a given email. There are 3 possible
	 * outputs
	 * <ul>
	 * <li>1. Citizen exists - returns reports + 200</li>
	 * <li>2. No Citizen exists for email - return 400</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return List of Reports
	 */
	@RequestMapping(value = "/report/{email:.+}", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getCitizenReports(@PathVariable("email") String email) {
		try {
			Citizen citz = citizenService.getCitizen(email);
			return new ResponseEntity<List<Report>>(citz.getReports(), HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<List<Report>>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}