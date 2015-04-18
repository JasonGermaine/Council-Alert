package com.jgermaine.fyp.rest.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.jgermaine.fyp.rest.model.CouncilAlertUser;

@Repository
@Transactional
public class CouncilAlertUserDao {

	@PersistenceContext
	private EntityManager entityManager;

	public CouncilAlertUser getByEmail(String email) throws NoResultException, NonUniqueResultException, Exception {
		return (CouncilAlertUser) entityManager.createQuery("from CouncilAlertUser where email = :email")
				.setParameter("email", email).getSingleResult();

	}

	public void createUser(CouncilAlertUser user) {
		entityManager.persist(user);
	}

	public void remove(CouncilAlertUser user) {
		entityManager.remove(user);
	}

	public void update(CouncilAlertUser user) {
		entityManager.merge(user);
	}
}
