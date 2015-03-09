package com.jgermaine.fyp.rest.model.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

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
	public void create(Report report) {
		entityManager.persist(report);
	}

	/**
	 * Delete the report from the database.
	 */
	public void delete(Report report) {
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
	public List<Report> getAll() {
		return entityManager.createQuery("from Report").getResultList();
	}

	/**
	 * Return the report having the passed name.
	 */
	public Report getByName(String name) {
		return (Report) entityManager
				.createQuery("from Report where name = :name")
				.setParameter("name", name).getSingleResult();
	}
	
	/**
	 * Return the report having the passed name.
	 */
	public Report getByEmployee(String email) {
		try {
			return (Report) entityManager
					.createNativeQuery("Select * from Reports where emp_email = :email", Report.class)
					.setParameter("email", email)
					.getSingleResult();
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Return the report having the passed id.
	 */
	public Report getById(int id) {
		return entityManager.find(Report.class, id);
	}

	/**
	 * Update the passed report in the database.
	 */
	public void update(Report report) {
		entityManager.merge(report);
		return;
	}

	/**
	 * Returns a list of reports sorted by closest proximity
	 * @param lat
	 * @param lon
	 * @return list of report
	 */
	@SuppressWarnings("unchecked")
	public List<Report> getNearestReport(double lat, double lon) {
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
	 * @param lat
	 * @param lon
	 * @return list of report
	 */
	@SuppressWarnings("unchecked")
	public List<Report> getUnassignedNearestReport(double lat, double lon) {
		Query query = entityManager
				.createNativeQuery(
						"SELECT *, "
							+ "( 6371 * acos( cos( radians(:lat) ) * cos( radians( latitude ) )"
							+ "* cos( radians( longitude ) - radians(:lon) ) "
							+ "+ sin( radians(:lat) ) * sin( radians( latitude ) ) ) )"
							+ "AS distance "
							+ "FROM Reports "
							+ "WHERE emp_email IS NULL "
							+ "AND status = false  "
							+ "HAVING distance < 10 "
							+ "ORDER BY distance "
							+ "LIMIT 0 , 6;",
						Report.class);
		query.setParameter("lat", lat);
		query.setParameter("lon", lon);
		return query.getResultList();
	}
}