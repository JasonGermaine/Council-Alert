package com.jgermaine.fyp.rest.controller;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.exception.ModelValidationException;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Message;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;
import com.jgermaine.fyp.rest.util.ResponseMessageUtil;

/**
 * Handles all RESTful operations for issue reporting
 * 
 * @author JasonGermaine
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
	 * @return reports
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getAllReports() {
		try {
			return new ResponseEntity<List<Report>>(reportService.getReports(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns a list of all submitted reports for the current day
	 * 
	 * @return reports
	 */
	@RequestMapping(value = "/today", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getTodayReports() {
		try {
			return new ResponseEntity<List<Report>>(reportService.getTodayReports(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns a list of complete reports
	 * 
	 * @return reports
	 */
	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getCompleteReports() {
		try {
			return new ResponseEntity<List<Report>>(reportService.getCompleteReports(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Returns a list of incomplete reports
	 * 
	 * @return reports
	 */
	@RequestMapping(value = "/incomplete", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getIncompleteReports() {
		try {
			return new ResponseEntity<List<Report>>(reportService.getIncompleteReports(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
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
			return new ResponseEntity<List<Report>>(reportService.getUnassignedNearReports(lat, lon), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves the appropriate Report for a given id. There are 3 possible
	 * outputs
	 * <ul>
	 * <li>1. Report for id exists - returns Report and 200</li>
	 * <li>2. Report for id does not exist - returns 400</li>
	 * </ul>
	 * 
	 * @param id
	 * @return report
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Report> retrieveReport(@PathVariable("id") int id) {
		try {
			return new ResponseEntity<Report>(reportService.getReport(id), HttpStatus.OK);
		} catch (NoResultException e) {
			return new ResponseEntity<Report>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Report>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Obtains a Citizen from the given email and creates the Report for that
	 * Citizen There are 3 possible outputs
	 * <ul>
	 * <li>1. Citizen for email exists - returns 201</li>
	 * <li>2. Citizen for email does not exist - returns 400</li>
	 * <li>3. Invalid Report passed - returns 400</li>
	 * <li>4. Report for id already exists - returns 409</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/citizen/{email:.+}", method = RequestMethod.POST)
	public ResponseEntity<Message> createReport(@PathVariable("email") String email, @RequestBody @Valid Report report,
			BindingResult result) {
		try {
			if (result.hasErrors())
				throw new ModelValidationException(result.getFieldErrorCount());

			Citizen c = citizenService.getCitizen(email.toLowerCase());
			c.addReport(report);
			report.setCitizen(c);
			reportService.addReport(report);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_REPORT_CREATION),
					HttpStatus.CREATED);
		} catch (EntityExistsException | DataIntegrityViolationException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_REPORT_EXIST), HttpStatus.CONFLICT);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_USER_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (ModelValidationException e) {
			return new ResponseEntity<Message>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);			
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Closes a given report. 
	 * There are 3 possible outputs
	 * <ul>
	 * <li>1. Report updated successfully - returns 201</li>
	 * <li>2. Citizen for email does not exist - returns 400</li>
	 * <li>3. Invalid Report passed - returns 400</li>
	 * <li>4. Report for id already exists - returns 409</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * 
	 * @param report
	 * @return HttpResponse
	 */
	@RequestMapping(value = "/close", method = RequestMethod.POST)
	public ResponseEntity<Message> completeReport(@RequestBody @Valid Report report, BindingResult result) {
		try {
			if (result.hasErrors())
				throw new ModelValidationException(result.getFieldErrorCount());

			Citizen citz = citizenService.getCitizen(report.getCitizenId());
			report.setEmployee(null);
			report.setCitizen(citz);
			reportService.updateReport(report);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_REPORT_UPDATE), HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_USER_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (ModelValidationException e) {
			return new ResponseEntity<Message>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves an Report for a given Employee email
	 * <ul>
	 * <li>1. Report exists - returns Report + 200</li>
	 * <li>2. No Report exists for email - return 400</li>
	 * <li>3. Unexpected error occurs - returns 500</li>
	 * </ul>
	 * @param email
	 * @return Report
	 */
	@RequestMapping(value = "/employee/{email:.+}", method = RequestMethod.GET)
	public ResponseEntity<Report> retrieveReportForEmp(@PathVariable("email") String email) {
		try {
			return new ResponseEntity<Report>(reportService.getReportForEmp(email.toLowerCase()), HttpStatus.OK);
		} catch (NoResultException | NonUniqueResultException e) {
			return new ResponseEntity<Report>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Report>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates the appropriate Report for a given id. There are 3 possible
	 * outputs
	 * <ul>
	 * <li>1. Report for id exists - update Report and return 200</li>
	 * <li>2. Report for id does not exist - returns 400</li>
	 * <li>3. Invalid comment entries - returns 400</li>
	 * <li>4. Unexpected error - returns 500</li>
	 * </ul>
	 * 
	 * @param id
	 * @return report
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Message> updateReport(@PathVariable("id") int id, @RequestBody @Valid Report report,
			BindingResult result) {
		try {
			if (result.hasErrors())
				throw new ModelValidationException(result.getFieldErrorCount());
			
			Report rpt = reportService.getReport(id);
			rpt.resetEntries(report.getEntries());
			reportService.updateReport(rpt);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.SUCCESS_REPORT_UPDATE), HttpStatus.OK);
		} catch (NoResultException e) {
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_REPORT_NOT_EXIST),
					HttpStatus.BAD_REQUEST);
		} catch (ModelValidationException e) {
			return new ResponseEntity<Message>(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new ResponseEntity<Message>(new Message(ResponseMessageUtil.ERROR_UNEXPECTED),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}