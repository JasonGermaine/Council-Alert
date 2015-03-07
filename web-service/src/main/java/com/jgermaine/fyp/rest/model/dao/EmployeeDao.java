package com.jgermaine.fyp.rest.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jgermaine.fyp.rest.controller.ReportController;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.Report;

/**
 * This class is used to access data for the Employee entity.
 */
@Repository
@Transactional
public class EmployeeDao {

	private static final Logger LOGGER = LogManager.getLogger(EmployeeDao.class
			.getName());

	// An EntityManager will be automatically injected from entityManagerFactory
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Create new Employee in the database.
	 */
	public void create(Employee employee) {
		entityManager.persist(employee);
		return;
	}

	/**
	 * Delete the Employee from the database.
	 */
	public void delete(Employee employee) {
		if (entityManager.contains(employee))
			entityManager.remove(employee);
		else
			entityManager.remove(entityManager.merge(employee));
		return;
	}

	/**
	 * Return all the Employees stored in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<Employee> getAll() {
		return entityManager.createQuery("from Employee").getResultList();
	}

	/**
	 * Return the Employee having the passed name.
	 */
	public Employee getByEmail(String email) {
		try {
			return (Employee) entityManager
					.createQuery("from Employee where email = :email")
					.setParameter("email", email).getSingleResult();
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	/**
	 * Return the Employee having no job assigned.
	 */
	@SuppressWarnings("unchecked")
	public List<Employee> getUnassigned() {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

			CriteriaQuery query = criteriaBuilder.createQuery(Employee.class);
			Root employee = query.from(Employee.class);
			query.select(employee);

			Subquery subquery = query.subquery(Report.class);
			Root subRootEntity = subquery.from(Report.class);
			subquery.select(subRootEntity);

			Predicate correlatePredicate = criteriaBuilder.equal(subRootEntity.get("employee"), employee);
			subquery.where(correlatePredicate);
			query.where(criteriaBuilder.not(criteriaBuilder.exists(subquery)));

			TypedQuery typedQuery = entityManager.createQuery(query);
			return typedQuery.getResultList();
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	/**
	 * Update the passed Employee in the database.
	 */
	public void update(Employee employee) {
		entityManager.merge(employee);
	}

}