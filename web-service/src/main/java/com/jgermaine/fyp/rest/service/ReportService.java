package com.jgermaine.fyp.rest.service;

import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Report;

@Service
public class ReportService {

	public Report getReport(){
		Report report = new Report();
		report.setId(1);
		report.setName("PotHole");
		return report;
	}
} 