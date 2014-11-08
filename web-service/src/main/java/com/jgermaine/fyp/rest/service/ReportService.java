package com.jgermaine.fyp.rest.service;

import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Report;

@Service
public class ReportService {
	
	public Report getReport(){
		Report r = new Report();
		r.setId(1);
		r.setName("PotHole");
		return r;
	}
} 