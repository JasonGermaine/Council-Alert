package com.jgermaine.fyp.rest.service;

import java.util.HashMap;
import java.util.List;

import com.jgermaine.fyp.rest.model.Report;

public interface ReportService {

	public Report getReport();
	
	public void addReport(Report report);
	
	public void removeReport(Report report);
	
	public List<Report> getReports();
	
	public List<Report> getTodayReports();
	
	public List<Report> getCompleteReports();
	
	public List<Report> getIncompleteReports();
	
	public Report getReport(String name);
	
	public Report getReportForEmp(String email);
	
	public Report getReport(int id);
	
	public void updateReport(Report report);
	
	public List<Report> getReports(double lat, double lon);
	
	public List<Report> getUnassignedNearReports(double lat, double lon);
	
	public HashMap<String, Long> getReportStatistics();
}
