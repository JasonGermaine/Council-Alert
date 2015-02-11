package com.jgermaine.fyp.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

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

	@RequestMapping("/")
	public String getListUsersView(Model model) {
		LOGGER.debug("Returning all reports");
		model.addAttribute("reports", reportService.getReports());
		return PREFIX + "/displayReport";
	}
	
	@ResponseBody
	@RequestMapping("/list")
	public List<Report> getListUsers() {
		LOGGER.debug("Returning all reports");
		return reportService.getReports();
	}

	@ResponseBody
	@RequestMapping("/unassigned")
	public List<Report> getReportDetail(
			@RequestParam(value = "lat", required = true) Double lat,
			@RequestParam(value = "lon", required = true) Double lon) {
		return reportService.getUnassignedNearReports(lat, lon);
	}

	@ResponseBody
	@RequestMapping("/get")
	public Report getReportDetail(HttpServletResponse response) {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

		return reportService.getReport(11);
	}

	@RequestMapping("/add")
	public String pushNewReport(Model model) {
		Report r = new Report();
		r.setName("SampleReport");
		r.setLongitude(-6.3623403);
		r.setLatitude(53.2895758);
		reportService.addReport(r);
		model.addAttribute("reports", reportService.getReports());
		return PREFIX + "/displayReport";
	}

	// This is a GET request
	@RequestMapping("/retrieve")
	@ResponseBody
	public Report retrieveReport(
			@RequestParam(value = "id", required = false) String id) {
		int reportId = 1;
		if (id != null && !id.isEmpty()) {
			try {
				reportId = Integer.parseInt(id);
			} catch (NumberFormatException nfe) {
				LOGGER.error(nfe);
			}
		}
		return reportService.getReport(reportId);
	}

	@RequestMapping("/send")
	@ResponseBody
	public ResponseEntity<String> postReportDetails(@RequestBody Report report) {
		LOGGER.info(String.format("Report name: %s, long: %s, lat: %s",
				report.getName(), report.getLongitude(), report.getLatitude()));
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