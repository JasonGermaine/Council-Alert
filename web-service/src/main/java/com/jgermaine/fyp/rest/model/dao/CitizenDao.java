package com.jgermaine.fyp.rest.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jgermaine.fyp.rest.model.Citizen;

/**
 * This class is used to access data for the Citizen entity.
 */
@Repository
@Transactional
public class CitizenDao {
   
  // An EntityManager will be automatically injected from entityManagerFactory
  @PersistenceContext
  private EntityManager entityManager;
   
  private static final Logger LOGGER = LogManager.getLogger(CitizenDao.class
			.getName());
  /**
   * Create new Citizen in the database.
   */
  public void create(Citizen Citizen) {
	  entityManager.persist(Citizen);
    return;
  }
   
  /**
   * Delete the Citizen from the database.
   */
  public void delete(Citizen Citizen) {
    if (entityManager.contains(Citizen))
    	entityManager.remove(Citizen);
    else
    	entityManager.remove(entityManager.merge(Citizen));
    return;
  }
   
  /**
   * Return all the Citizens stored in the database.
   */
  @SuppressWarnings("unchecked")
  public List<Citizen> getAll() {
    return entityManager.createQuery("from Citizen").getResultList();
  }
   
  /**
   * Return the Citizen having the passed name.
   */
  public Citizen getByEmail(String email) {
	try {
	    return (Citizen) entityManager.createQuery(
	        "from Citizen where email = :email", Citizen.class)
	        .setParameter("email", email)
	        .getSingleResult();
	} catch (Exception e) {
		LOGGER.error("Failed ", e);
		return null;
	}
  }
 
  /**
   * Update the passed Citizen in the database.
   */
  public void update(Citizen Citizen) {
	entityManager.merge(Citizen);
    return;
  }
 
} 