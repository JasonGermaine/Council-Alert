package com.jgermaine.fyp.rest.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.Report;

/**
 * This class is used to access data for the Employee entity.
 */
@Repository
@Transactional
public class EmployeeDao {

	// An EntityManager will be automatically injected from entityManagerFactory
	@PersistenceContext
	private EntityManager entityManager;

	private static final int SET_SIZE = 30;

	/**
	 * Delete the Employee from the database.
	 */
	public void delete(Employee employee) throws Exception {
		entityManager.remove(employee);
	}

	/**
	 * Return all the Employees stored in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<Employee> getAll(int index) throws Exception {
		return entityManager.createQuery("from Employee").setFirstResult(index).setMaxResults(SET_SIZE).getResultList();
	}

	/**
	 * Return the Employee having the passed name.
	 */
	public Employee getByEmail(String email) throws NoResultException, NonUniqueResultException, Exception {
		return (Employee) entityManager.createQuery("from Employee where email = :email").setParameter("email", email)
				.getSingleResult();
	}

	public Long getAllCount() {
		try {
			return (Long) entityManager.createQuery("Select count(*) from Employee").getSingleResult();
		} catch (Exception e) {
			return (long) 0;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Long getUnassignedCount() {
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

			// Select count from employee
			CriteriaQuery query = criteriaBuilder.createQuery(Long.class);
			Root employee = query.from(Employee.class);
			query.select(criteriaBuilder.count(employee));

			// Create subquery for class
			Subquery subquery = query.subquery(Report.class);
			Root subRootEntity = subquery.from(Report.class);
			subquery.select(subRootEntity);

			// where employee does not exist in reports
			Predicate correlatePredicate = criteriaBuilder.equal(subRootEntity.get("employee"), employee);
			subquery.where(correlatePredicate);
			query.where(criteriaBuilder.not(criteriaBuilder.exists(subquery)));

			TypedQuery typedQuery = entityManager.createQuery(query);
			return (Long) typedQuery.getSingleResult();
		} catch (Exception e) {
			return (long) 0;
		}
	}

	/**
	 * Return the Employee having no job assigned.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Employee> getUnassigned(int index) throws Exception {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		// Select * from employee
		CriteriaQuery query = criteriaBuilder.createQuery(Employee.class);
		Root employee = query.from(Employee.class);
		query.select(employee);

		// Create subquery for class
		Subquery subquery = query.subquery(Report.class);
		Root subRootEntity = subquery.from(Report.class);
		subquery.select(subRootEntity);

		// where employee does exist in reports
		Predicate correlatePredicate = criteriaBuilder.equal(subRootEntity.get("employee"), employee);
		subquery.where(correlatePredicate);
		query.where(criteriaBuilder.not(criteriaBuilder.exists(subquery)));

		TypedQuery typedQuery = entityManager.createQuery(query);
		return typedQuery.setFirstResult(index).setMaxResults(SET_SIZE).getResultList();
	}

	/**
	 * Update the passed Employee in the database.
	 */
	public void update(Employee employee) throws Exception {
		entityManager.merge(employee);
	}

	/**
	 * Returns a list of unassigned reports sorted by closest proximity /**
	 * 
	 * @see http://en.wikipedia.org/wiki/Haversine_formula
	 * @param lat
	 * @param lon
	 * @return list of report
	 */
	@SuppressWarnings("unchecked")
	public List<Employee> getUnassignedNearestEmployee(double lat, double lon) throws Exception {
		Query query = entityManager.createNativeQuery("SELECT *, "
				+ "( 6371 * acos( cos( radians(:lat) ) * cos( radians( latitude ) )"
				+ "* cos( radians( longitude ) - radians(:lon) ) "
				+ "+ sin( radians(:lat) ) * sin( radians( latitude ) ) ) )" + "AS distance " + "FROM Employee "
				+ "WHERE email not in(Select r.emp_email " + "from Reports r WHERE r.emp_email IS NOT NULL) "
				+ "HAVING distance < 10 " + "ORDER BY distance " + "LIMIT 0 , 10;", Employee.class);
		query.setParameter("lat", lat);
		query.setParameter("lon", lon);
		return query.getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Employee> getAssigned(int index) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		// Select * from employee
		CriteriaQuery query = criteriaBuilder.createQuery(Employee.class);
		Root employee = query.from(Employee.class);
		query.select(employee);

		// Create subquery for class
		Subquery subquery = query.subquery(Report.class);
		Root subRootEntity = subquery.from(Report.class);
		subquery.select(subRootEntity);

		// where employee does exist in reports
		Predicate correlatePredicate = criteriaBuilder.equal(subRootEntity.get("employee"), employee);
		subquery.where(correlatePredicate);
		query.where(criteriaBuilder.exists(subquery));

		TypedQuery typedQuery = entityManager.createQuery(query);
		return typedQuery.setFirstResult(index).setMaxResults(SET_SIZE).getResultList();
	}
}