package com.jgermaine.fyp.rest.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.dao.ReportDao;
import com.jgermaine.fyp.rest.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

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
	
	public void updateReport(Report report) {
		reportDao.update(report);
	}
	
	public List<Report> getReports(double lat, double lon) {
		return reportDao.getNearestReport(lat, lon);
	}
} 