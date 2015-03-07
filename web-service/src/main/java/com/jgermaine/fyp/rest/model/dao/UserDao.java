package com.jgermaine.fyp.rest.model.dao;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.jgermaine.fyp.rest.model.User;

@Repository
@Transactional
public class UserDao {

	// An EntityManager will be automatically injected from entityManagerFactory
	@PersistenceContext
	private EntityManager entityManager;

	public User getUser(String email) {
		try {
			return (User) entityManager
					.createQuery("from User "
							+ "where email = :email")
					.setParameter("email", email)
					.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

}
