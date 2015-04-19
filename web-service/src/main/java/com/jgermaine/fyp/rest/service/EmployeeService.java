package com.jgermaine.fyp.rest.service;

import java.util.HashMap;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.jgermaine.fyp.rest.model.Employee;

public interface EmployeeService {

	Employee getEmployee();

	void removeEmployee(Employee employee) throws Exception;

	List<Employee> getEmployees() throws Exception;

	Employee getEmployee(String email) throws NoResultException, NonUniqueResultException, Exception;

	List<Employee> getUnassignedEmployees() throws Exception;

	List<Employee> getAssignedEmployees() throws Exception;

	List<Employee> getUnassignedNearEmployees(double lat, double lon) throws Exception;

	HashMap<String, Long> getEmployeesStatistics() throws Exception;
}
