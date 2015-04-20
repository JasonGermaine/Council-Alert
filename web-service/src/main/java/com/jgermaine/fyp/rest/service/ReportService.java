package com.jgermaine.fyp.rest.service;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import org.springframework.dao.DataIntegrityViolationException;

import com.jgermaine.fyp.rest.model.Report;

public interface ReportService {

	Report getReport();

	void addReport(Report report) throws EntityExistsException, PersistenceException,
		DataIntegrityViolationException, Exception;

	void removeReport(Report report) throws Exception;

	List<Report> getReports(int index) throws Exception;

	List<Report> getTodayReports(int index) throws Exception;

	List<Report> getCompleteReports(int index) throws Exception;

	List<Report> getIncompleteReports(int index) throws Exception;

	Report getReport(String name) throws NoResultException, NonUniqueResultException, Exception;

	Report getReportForEmp(String email) throws NoResultException, NonUniqueResultException, Exception;

	Report getReport(int id) throws NoResultException;

	void updateReport(Report report) throws Exception;

	List<Report> getReports(double lat, double lon) throws Exception;

	List<Report> getUnassignedNearReports(double lat, double lon) throws Exception;

	HashMap<String, Long> getReportStatistics();
}
