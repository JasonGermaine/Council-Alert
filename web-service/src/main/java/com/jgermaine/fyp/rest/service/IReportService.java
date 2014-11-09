package com.jgermaine.fyp.rest.service;

import java.util.List;

import com.jgermaine.fyp.rest.model.Report;

public interface IReportService {

	public Report getReport();
	
	public void addReport(Report report);
	
	public void removeReport(Report report);
	
	public List<Report> getReports();
	
	public Report getReport(String name);
	
	public Report getReport(int id);
}
