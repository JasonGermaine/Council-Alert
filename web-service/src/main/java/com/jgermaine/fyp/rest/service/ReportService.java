package com.jgermaine.fyp.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.ReportDao;

@Service
public class ReportService implements IReportService {

	@Autowired
	private ReportDao reportDao;
	
	
	public Report getReport(){
		Report report = new Report();
		report.setId(1);
		report.setName("PotHole");
		return report;
	}
	
	public void addReport(Report report) {
		reportDao.create(report);
	}
	
	public void removeReport(Report report) {
		reportDao.delete(report);;
	}
	
	public List<Report> getReports() {
		return reportDao.getAll();
	}
	
	public Report getReport(String name) {
		return reportDao.getByName(name);
	}
	
	public Report getReport(int id) {
		return reportDao.getById(id);
	}
} 