package com.jgermaine.fyp.rest.service;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import com.jgermaine.fyp.rest.model.Employee;

public interface EmployeeService {

	public Employee getEmployee();
	
	public void addEmployee(Employee employee) throws EntityExistsException, PersistenceException, Exception;
	
	public void removeEmployee(Employee employee) throws Exception;
	
	public List<Employee> getEmployees() throws Exception;
	
	public Employee getEmployee(String email) throws NoResultException, NonUniqueResultException, Exception;
	
	public List<Employee> getUnassignedEmployees() throws Exception;
	
	public List<Employee> getAssignedEmployees() throws Exception;
	
	public List<Employee> getUnassignedNearEmployees(double lat, double lon) throws Exception;
	
	public HashMap<String, Long> getEmployeesStatistics() throws Exception;
}
