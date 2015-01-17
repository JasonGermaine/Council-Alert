package com.jgermaine.fyp.rest.model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.jgermaine.fyp.rest.model.Employee;

/**
 * This class is used to access data for the Employee entity.
 */
@Repository
@Transactional
public class EmployeeDao {
   
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
	    return (Employee) entityManager.createQuery(
	        "from Employee where email = :email")
	        .setParameter("email", email)
	        .getSingleResult();
	} catch (Exception e) {
		return null;
	}
  }

  /**
   * Return the Employee having no job assigned.
   */
  @SuppressWarnings("unchecked")
  public List<Employee> getUnassigned() {
	try {
	    return  entityManager.createQuery(
	        "from Employee where assigned = false")
	        .getResultList();
	} catch (Exception e) {
		return null;
	}
  }
  
  /**
   * Update the passed Employee in the database.
   */
  public void update(Employee employee) {
	entityManager.merge(employee);
    return;
  }
 
} 