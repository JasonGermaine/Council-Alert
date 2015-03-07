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
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;


/**
 * Handles all RESTful operations for issue reporting
 * @author jason
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

	private static final Logger LOGGER = LogManager
			.getLogger(ReportController.class.getName());

	@Autowired
	private ReportServiceImpl reportService;

	@Autowired
	private CitizenServiceImpl citizenService;
	
	/**
	 * Returns a list of all submitted reports
	 * @return all reports
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public ResponseEntity<List<Report>> getAllReports() {
		LOGGER.info("Returning all reports");
		return new ResponseEntity<List<Report>>(reportService.getReports(), HttpStatus.OK);
	}

	
	/**
	 * Returns a list of of reports of maximum size <i>x</i>.
	 * Reports are ordered by the distance to the latitude and longitude provided
	 * @param lat
	 * @param lon
	 * @return list of nearest reports
	 */
	@RequestMapping(value="/open", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getNearestOpenReports(
			@RequestParam(value = "lat", required = true) Double lat,
			@RequestParam(value = "lon", required = true) Double lon) {
		LOGGER.info("Returning all open reports");
		return new ResponseEntity<List<Report>>(reportService.getUnassignedNearReports(lat, lon), HttpStatus.OK);
	}

	/**
	 * Retrieves the appropriate Report for a given id.
	 * There are 3 possible outputs
	 * <ul>
	 * <li>1. Report for id exists - returns Report and 200</li>
	 * <li>2. Report for id does not exist - returns 404</li>
	 * <li>3. Invalid id passed in - returns 400</li>
	 * </ul>
	 * @param report id
	 * @return report
	 */
	@RequestMapping(value="/get", method = RequestMethod.GET)
	public ResponseEntity<Report> retrieveReport(
			@RequestParam(value = "id", required = true) String id) {
		LOGGER.info("Recieved GET for report: " + id);
		try {
			int reportId = Integer.parseInt(id);
			Report report = reportService.getReport(reportId);
			
			if (report != null) {
				LOGGER.info("Successful GET for id " + id);
				return new ResponseEntity<Report>(report, HttpStatus.OK);
			} else {
				LOGGER.warn(id + " does not exist.");
				return new ResponseEntity<Report>(HttpStatus.NOT_FOUND);
			}
		} catch (NumberFormatException nfe) {
			LOGGER.warn("Bad Request for id " + id);
			return new ResponseEntity<Report>(HttpStatus.BAD_REQUEST);
		}	
	}

	/**
	 * Obtains a Citizen from the given email and creates the Report for that Citizen
	 * There are 3 possible outputs
	 * <ul>
	 * <li>1. Citizen for email exists - returns 201</li>
	 * <li>2. Citizen for email does not exist - returns 400</li>
	 * <li>3. Invalid Report passed - returns 400</li>
	 * </ul>
	 * @return HttpResponse
	 */
	@RequestMapping(value="/create", method = RequestMethod.POST)
	public ResponseEntity<String> createReport(@RequestParam(value = "email", required=true) String email,
			@RequestBody @Valid Report report) {
		LOGGER.info("Posting report for Citizen: " + email);
		Citizen c = citizenService.getCitizen(email);
		if (c != null) {
			c.addReport(report);
			report.setCitizen(c);
			reportService.addReport(report);
			
			LOGGER.info(String.format("Successfully Added Report: %s for Citizen: %s",
					report.getName(), email));
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} else {
			LOGGER.error("Citizen does not exist in DB: " + email);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Closes a given report providing it is valid
	 * @param report
	 * @return HttpResponse
	 */
	@RequestMapping(value="/close", method = RequestMethod.POST)
	public ResponseEntity<String> completeReport(@RequestBody @Valid Report report) {
		LOGGER.info("Closed Report: " + report.getId());
		report.setEmployee(null);
		report.setCitizen(citizenService.getCitizen(report.getCitizenId()));
		reportService.updateReport(report);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
}