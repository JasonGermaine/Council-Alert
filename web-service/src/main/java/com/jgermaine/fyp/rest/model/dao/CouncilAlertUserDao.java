package com.jgermaine.fyp.rest.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.User;

@Repository
@Transactional
public class CouncilAlertUserDao {

	private static final Logger LOGGER = LogManager.getLogger(CouncilAlertUserDao.class
			.getName());
	
	@PersistenceContext
	private EntityManager entityManager;
	  
	public CouncilAlertUser getByEmail(String email) {
		try {
			return (CouncilAlertUser) entityManager
				.createQuery("from CouncilAlertUser where email = :email")
				.setParameter("email", email).getSingleResult();
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	public void createUser(CouncilAlertUser user) {
		entityManager.persist(user);
	}

	public void remove(CouncilAlertUser user) {
		entityManager.remove(user);
	}
}
