package com.jgermaine.fyp.rest.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

	private static final Logger LOGGER = LogManager.getLogger(ReportController.class.getName());

	@Autowired
	private ReportService reportService;

	@RequestMapping("/get")
	public Report getReportDetail() {
		return reportService.getReport(6);
	}
	
	@RequestMapping("/post")
	public Report postReportDetails(@RequestBody Report report) {
		LOGGER.info(String.format("Report id: %s, name: %s", report.getId(),
				report.getName()));
		reportService.addReport(report);
		return reportService.getReport(report.getName());
	}
	
	
}