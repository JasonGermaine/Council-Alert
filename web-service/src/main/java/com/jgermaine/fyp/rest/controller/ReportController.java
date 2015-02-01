package com.jgermaine.fyp.rest.controller;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jgermaine.fyp.rest.gcm.GcmOperations;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;

@Controller
@RequestMapping("/report")
public class ReportController {

	private static final String PREFIX = "report";
	private static final Logger LOGGER = LogManager
			.getLogger(ReportController.class.getName());

	@Autowired
	private ReportServiceImpl reportService;

	@RequestMapping("/push")
	public void sendPush() {
		sendNotifcation();
	}

	// This is a GET request
	@ResponseBody
	@RequestMapping("/get")
	public Report getReportDetail() {
		sendNotifcation();
		return reportService.getReport(1);
	}

	// This is a GET request
	@RequestMapping("/retrieve")
	@ResponseBody
	public Report retrieveReport(@RequestParam(value = "id", required = false) String id) {
		int reportId = 1;
		if (id != null && !id.isEmpty()) {
			try {
				reportId = Integer.parseInt(id);
			} catch(NumberFormatException nfe) {
				LOGGER.error(nfe);
			}
		}
		return reportService.getReport(reportId);
	}

	public void sendNotifcation() {
		try {
			GcmOperations.sendNotifcation();
		} catch (IOException e) {
			LOGGER.error("Couldnt send GCM post", e);
		}
	}

	@RequestMapping("/send")
	@ResponseBody
	public ResponseEntity<String> postReportDetails(@RequestBody Report report) {
		LOGGER.info(String.format("Report id: %s, name: %s, long: %s, lat: %s",
				report.getId(), report.getName(), report.getLongitude(),
				report.getLatitude()));
		reportService.addReport(report);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping("/complete")
	@ResponseBody
	public ResponseEntity<String> completeReport(@RequestBody Report report) {
		LOGGER.info(String.format("Report id: %s, name: %s, long: %s, lat: %s",
				report.getId(), report.getName(), report.getLongitude(),
				report.getLatitude()));
		report.setEmployee(null);
		reportService.updateReport(report);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping("/display")
	public String getListUsersView(Model model) {
		LOGGER.debug("Returning all reports");
		model.addAttribute("reports", reportService.getReports());
		return PREFIX + "/displayReport";
	}

	@RequestMapping("/proximity")
	public String getCloseReports(Model model) {
		LOGGER.debug("Returning reports based on a proximity");
		model.addAttribute("reports",
				reportService.getReports(53.2895758, -6.3623403));
		return PREFIX + "/displayReport";
	}

	@RequestMapping("/map")
	public String getMapView(Model model) {
		model.addAttribute("reports", reportService.getReports());
		return PREFIX + "/displayMap";
	}
}