package com.jgermaine.fyp.rest.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.dao.EmployeeDao;
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
	
	public void addReport(Report report) throws EntityExistsException, PersistenceException, Exception {
		reportDao.create(report);
	}
	
	public void removeReport(Report report) throws Exception {
		reportDao.delete(report);;
	}
	
	public List<Report> getReports() throws Exception {
		return reportDao.getAll();
	}
	
	public List<Report> getTodayReports() throws Exception {
		return reportDao.getTodaysReports();
	}
	
	public Report getReport(String name) throws NoResultException, NonUniqueResultException, Exception {
		return reportDao.getByName(name);
	}
	
	public Report getReport(int id) throws NoResultException {
		return reportDao.getById(id);
	}
	
	public void updateReport(Report report) throws Exception {
		reportDao.update(report);
	}
	
	public List<Report> getReports(double lat, double lon) throws Exception {
		return reportDao.getNearestReport(lat, lon);
	}
	
	public List<Report> getUnassignedNearReports(double lat, double lon) throws Exception {
		return reportDao.getUnassignedNearestReport(lat, lon);
	}

	@Override
	public Report getReportForEmp(String email) throws NoResultException, NonUniqueResultException, Exception {
		return reportDao.getByEmployee(email);
	}

	@Override
	public HashMap<String, Long> getReportStatistics() {
		HashMap<String, Long> statMap = new HashMap<String, Long>();
		
		statMap.put("report_today", reportDao.getTodayReportCount());
		statMap.put("report_complete", reportDao.getCompleteReportCount());
		statMap.put("report_incomplete", reportDao.getIncompleteReportCount());	
		
		return statMap;
	}
	
	public List<Report> getCompleteReports() throws Exception {
		return reportDao.getComplete();
	}
	
	public List<Report> getIncompleteReports() throws Exception {
		return reportDao.getIncomplete();
	}
} 