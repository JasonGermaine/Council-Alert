package com.jgermaine.fyp.rest.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jgermaine.fyp.rest.gcm.GcmOperations;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

	private static final Logger LOGGER = LogManager
			.getLogger(ReportController.class.getName());

	@Autowired
	private ReportService reportService;

	// This is a GET request
	@RequestMapping("/get")
	public Report getReportDetail() {
		sendNotifcation();
		return reportService.getReport(1);
	}

	// This is a GET request
	@RequestMapping("/retrieve")
	public Report retrieveReport() {
		return reportService.getReport(12);
	}
	
	public void sendNotifcation() {
		try {
			GcmOperations.sendNotifcation();
		} catch (IOException e) {
			LOGGER.error("Couldnt send GCM post", e);
			e.printStackTrace();
		}
	}

	@RequestMapping("/send")
	public Report postReportDetails(@RequestBody Report report) {
		LOGGER.info(String.format("Report id: %s, name: %s, long: %s, lat: %s",
				report.getId(), report.getName(), report.getLongitude(),
				report.getLatitude()));
		reportService.addReport(report);
		return report;
	}

	@RequestMapping("/complete")
	public Report completeReport(@RequestBody Report report) {
		LOGGER.info(String.format("Report id: %s, name: %s, long: %s, lat: %s",
				report.getId(), report.getName(), report.getLongitude(),
				report.getLatitude()));
		reportService.updateReport(report);
		return report;
	}
	
	@RequestMapping("/display")
	public ModelAndView getListUsersView() {	
		 ModelAndView model = new ModelAndView();
		 model.setViewName("displayReport"); 
		 model.addObject("reports",reportService.getReports()); 
		 return model;
	}

	@RequestMapping("/map")
	public ModelAndView getMapView() {	
		 ModelAndView model = new ModelAndView();
		 model.setViewName("displayMap"); 
		 model.addObject("reports",reportService.getReports()); 
		 return model;
	}
}