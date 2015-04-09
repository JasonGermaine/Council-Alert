package com.jgermaine.fyp.rest.controller;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
 * 
 * @author jason
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

	private static final Logger LOGGER = LogManager.getLogger(ReportController.class.getName());

	@Autowired
	private ReportServiceImpl reportService;

	@Autowired
	private CitizenServiceImpl citizenService;

	/**
	 * Returns a list of all submitted reports
	 * 
	 * @return all reports
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getAllReports() {
		try {
			LOGGER.info("Returning all reports");
			return new ResponseEntity<List<Report>>(reportService.getReports(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/today", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getTodayReports() {
		try {
			LOGGER.info("Returning all reports");
			return new ResponseEntity<List<Report>>(reportService.getTodayReports(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getCompleteReports() {
		try {
			LOGGER.info("Returning all reports");
			return new ResponseEntity<List<Report>>(reportService.getCompleteReports(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/incomplete", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getIncompleteReports() {
		try {
			LOGGER.info("Returning all reports");
			return new ResponseEntity<List<Report>>(reportService.getIncompleteReports(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns a list of of reports of maximum size <i>x</i>. Reports are
	 * ordered by the distance to the latitude and longitude provided
	 * 
	 * @param lat
	 * @param lon
	 * @return list of nearest reports
	 */
	@RequestMapping(value = "/open", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getNearestOpenReports(@RequestParam(value = "lat", required = true) Double lat,
			@RequestParam(value = "lon", required = true) Double lon) {
		try {
			LOGGER.info("Returning all open reports");
			return new ResponseEntity<List<Report>>(reportService.getUnassignedNearReports(lat, lon), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves the appropriate Report for a given id. There are 3 possible
	 * outputs
	 * <ul>
	 * <li>1. Report for id exists - returns Report and 200</li>
	 * <li>2. Report for id does not exist - returns 404</li>
	 * <li>3. Invalid id passed in - returns 400</li>
	 * </ul>
	 * 
	 * @param report
	 *            id
	 * @return report
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Report> retrieveReport(@PathVariable("id") int id) {
		LOGGER.info("Recieved GET for report: " + id);
		try {
			Report report = reportService.getReport(id);
			return new ResponseEntity<Report>(report, HttpStatus.OK);
		} catch (NoResultException e) {
			return new ResponseEntity<Report>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Obtains a Citizen from the given email and creates the Report for that
	 * Citizen There are 3 possible outputs
	 * <ul>
	 * <li>1. Citizen for email exists - returns 201</li>
	 * <li>2. Citizen for email does not exist - returns 400</li>
	 * <li>3. Invalid Report passed - returns 400</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> createReport(@RequestBody @Valid Report report) {
		try {
			String email = report.getCitizenId();
			Citizen c = citizenService.getCitizen(email);
			c.addReport(report);
			report.setCitizen(c);
			reportService.addReport(report);

			LOGGER.info(String.format("Successfully Added Report: %s for Citizen: %s", report.getName(), email));
			return new ResponseEntity<String>(HttpStatus.CREATED);

		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Closes a given report providing it is valid
	 * 
	 * @param report
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/close", method = RequestMethod.POST)
	public ResponseEntity<String> completeReport(@RequestBody @Valid Report report) {
		try {
			LOGGER.info("Closed Report: " + report.getId());
			Citizen citz = citizenService.getCitizen(report.getCitizenId());
			report.setEmployee(null);
			LOGGER.info(report.getCitizenId());
			report.setCitizen(citz);
			reportService.updateReport(report);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/employee/{email:.+}", method = RequestMethod.GET)
	public ResponseEntity<Report> retrieveReportForEmp(@PathVariable("email") String email) {
		try {
			Report report = reportService.getReportForEmp(email);
			return new ResponseEntity<Report>(report, HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Report>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<Report>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}