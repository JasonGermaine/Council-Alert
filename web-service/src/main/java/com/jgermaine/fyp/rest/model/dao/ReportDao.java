package com.jgermaine.fyp.rest.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

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
    return;
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
    return (Report) entityManager.createQuery(
        "from Report where name = :name")
        .setParameter("name", name)
        .getSingleResult();
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
  
  @SuppressWarnings("unchecked")
  public List<Report> getNearestReport(double lat, double lon) {
	  Query query = entityManager.createNativeQuery(
			"SELECT *, ( 6371 * acos( cos( radians(:lat) ) * cos( radians( latitude ) )" 
			+ "* cos( radians( longitude ) - radians(:lon) ) + sin( radians(:lat) ) * sin( radians( latitude ) ) ) )"
			+ "AS distance FROM report HAVING distance < 10 ORDER BY distance LIMIT 0 , 20;", Report.class);
	  query.setParameter("lat", lat);
	  query.setParameter("lon", lon);
	  return query.getResultList();
  } 
} 