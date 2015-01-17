package com.jgermaine.fyp.rest.service;

import java.util.List;

import com.jgermaine.fyp.rest.model.Employee;


public interface EmployeeService {

	public Employee getEmployee();
	
	public void addEmployee(Employee employee);
	
	public void removeEmployee(Employee employee);
	
	public List<Employee> getEmployees();
	
	public Employee getEmployee(String email);
	
	public List<Employee> getUnassignedEmployees();
}
