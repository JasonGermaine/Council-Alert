package com.jgermaine.fyp.rest.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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

	private static final int SET_SIZE = 30;
	
	/**
	 * Delete the Citizen from the database.
	 */
	public void delete(Citizen Citizen) throws Exception {
		if (entityManager.contains(Citizen)) {
			entityManager.remove(Citizen);
		} else {
			entityManager.remove(entityManager.merge(Citizen));
		}
	}

	/**
	 * Return all the Citizens stored in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<Citizen> getAll(int index) throws Exception {
		return entityManager.createQuery("from Citizen").setFirstResult(index).setMaxResults(SET_SIZE).getResultList();
	}

	/**
	 * Return the Citizen having the passed name.
	 */
	public Citizen getByEmail(String email) throws NoResultException, NonUniqueResultException, Exception {
		return (Citizen) entityManager.createQuery("from Citizen where email = :email", Citizen.class)
				.setParameter("email", email).getSingleResult();
	}

	/**
	 * Update the passed Citizen in the database.
	 */
	public void update(Citizen Citizen) throws Exception {
		entityManager.merge(Citizen);
	}
}