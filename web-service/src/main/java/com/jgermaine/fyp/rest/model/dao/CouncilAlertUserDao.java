package com.jgermaine.fyp.rest.model.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.User;

/**
 * 
 * @author JasonGermaine
 *
 * The CouncilAlertUserDao applies the Transactional attribute to it's methods.
 * This implies that transaction management is implemented when carry out single or
 * multiple transactions with effective rollback implementation on errors.
 */
@Repository
@Transactional
public class CouncilAlertUserDao {

	@PersistenceContext
	private EntityManager entityManager;

	public CouncilAlertUser getByEmail(String email) throws NoResultException, NonUniqueResultException, Exception {
		return (CouncilAlertUser) entityManager.createQuery("from CouncilAlertUser where email = :email")
				.setParameter("email", email).getSingleResult();

	}

	public void createUser(CouncilAlertUser user, User newUser) throws EntityExistsException, PersistenceException,
			DataIntegrityViolationException, Exception {
		entityManager.persist(user);
		entityManager.persist(newUser);
	}

	public void remove(CouncilAlertUser user, User usr) {
		entityManager.remove(usr);
		entityManager.remove(user);
	}

	public void update(CouncilAlertUser user) {
		entityManager.merge(user);
	}
}
