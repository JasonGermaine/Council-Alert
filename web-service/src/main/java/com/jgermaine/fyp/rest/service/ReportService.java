package com.jgermaine.fyp.rest.service;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import com.jgermaine.fyp.rest.model.Report;

public interface ReportService {

	public Report getReport();

	public void addReport(Report report) throws EntityExistsException, PersistenceException,
		DataIntegrityViolationException, Exception;

	public void removeReport(Report report) throws Exception;

	public List<Report> getReports() throws Exception;

	public List<Report> getTodayReports() throws Exception;

	public List<Report> getCompleteReports() throws Exception;

	public List<Report> getIncompleteReports() throws Exception;

	public Report getReport(String name) throws NoResultException, NonUniqueResultException, Exception;

	public Report getReportForEmp(String email) throws NoResultException, NonUniqueResultException, Exception;

	public Report getReport(int id) throws NoResultException;

	public void updateReport(Report report) throws Exception;

	public List<Report> getReports(double lat, double lon) throws Exception;

	public List<Report> getUnassignedNearReports(double lat, double lon) throws Exception;

	public HashMap<String, Long> getReportStatistics();
}
