package com.jgermaine.fyp.rest.model.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.transaction.Transactional;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

import com.jgermaine.fyp.rest.model.Report;

/**
 * This class is used to access data for the Report entity.
 */
@Repository
@Transactional
public class ReportDao {

	// An EntityManager will be automatically injected from entityManagerFactory
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Create new report in the database.
	 */
	public void create(Report report) throws EntityExistsException, PersistenceException, DataIntegrityViolationException,
			Exception {
		entityManager.persist(report);
	}

	/**
	 * Delete the report from the database.
	 */
	public void delete(Report report) throws Exception {
		if (entityManager.contains(report))
			entityManager.remove(report);
		else
			entityManager.remove(entityManager.merge(report));
		return;
	}

	/**
	 * Return all the reports stored in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<Report> getAll() throws Exception {
		return entityManager.createQuery("from Report").getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Report> getWhereStatus(boolean status) throws Exception {
		return entityManager.createQuery("from Report where status = :status").setParameter("status", status)
				.getResultList();
	}

	public List<Report> getComplete() throws Exception {
		return getWhereStatus(true);
	}

	public List<Report> getIncomplete() throws Exception {
		return getWhereStatus(false);
	}

	/**
	 * Return the report having the passed name.
	 */
	public Report getByName(String name) throws NoResultException, NonUniqueResultException, Exception {
		return (Report) entityManager.createQuery("from Report where name = :name").setParameter("name", name)
				.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Report> getTodaysReports() throws Exception {
		try {
			return (List<Report>) entityManager.createNativeQuery(
					"Select * from Reports where timestamp > current_date()", Report.class).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the report having the passed name.
	 */
	public Report getByEmployee(String email) throws NoResultException, NonUniqueResultException, Exception {
		try {
			return (Report) entityManager
					.createNativeQuery("Select * from Reports where emp_email = :email", Report.class)
					.setParameter("email", email).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the report having the passed id.
	 */
	public Report getById(int id) throws NoResultException {
		Report report = entityManager.find(Report.class, id);
		if (report != null) {
			return report;
		} else {
			throw new NoResultException();
		}
	}

	/**
	 * Update the passed report in the database.
	 */
	public void update(Report report) throws Exception {
		entityManager.merge(report);
	}

	public long getCountFromQuery(String queryString) {
		try {
			Query query = entityManager.createQuery(queryString);
			return (long) query.getSingleResult();
		} catch (Exception e) {
			return 0;
		}
	}

	public long getIncompleteReportCount() {
		return getCountFromQuery("select count(*) from Report where status = false");
	}

	public long getCompleteReportCount() {
		return getCountFromQuery("select count(*) from Report where status = true");
	}

	public long getTodayReportCount() {
		return getCountFromQuery("select count(*) from Report where timestamp > current_date()");
	}

	// select * from Reports where timestamp BETWEEN '2015-03-30 00:00:00' AND
	// '2015-03-30 23:59:59';
	// where myDateProperty > current_date()
	// select count(*) from Reports where status = false;

	/**
	 * Returns a list of reports sorted by closest proximity
	 * 
	 * @param lat
	 * @param lon
	 * @return list of report
	 */
	@SuppressWarnings("unchecked")
	public List<Report> getNearestReport(double lat, double lon) throws Exception {
		Query query = entityManager
				.createNativeQuery(
						"SELECT *, ( 6371 * acos( cos( radians(:lat) ) * cos( radians( latitude ) )"
								+ "* cos( radians( longitude ) - radians(:lon) ) + sin( radians(:lat) ) * sin( radians( latitude ) ) ) )"
								+ "AS distance FROM Reports HAVING distance < 10 ORDER BY distance LIMIT 0 , 6;",
						Report.class);
		query.setParameter("lat", lat);
		query.setParameter("lon", lon);
		return query.getResultList();
	}

	/**
	 * Returns a list of unassigned reports sorted by closest proximity
	 * 
	 * @param lat
	 * @param lon
	 * @return list of report
	 */
	@SuppressWarnings("unchecked")
	public List<Report> getUnassignedNearestReport(double lat, double lon) throws Exception {
		Query query = entityManager.createNativeQuery("SELECT *, "
				+ "( 6371 * acos( cos( radians(:lat) ) * cos( radians( latitude ) )"
				+ "* cos( radians( longitude ) - radians(:lon) ) "
				+ "+ sin( radians(:lat) ) * sin( radians( latitude ) ) ) )" + "AS distance " + "FROM Reports "
				+ "WHERE emp_email IS NULL " + "AND status = false  " + "HAVING distance < 10 " + "ORDER BY distance "
				+ "LIMIT 0 , 6;", Report.class);
		query.setParameter("lat", lat);
		query.setParameter("lon", lon);
		return query.getResultList();
	}
}