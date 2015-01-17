package com.jgermaine.fyp.rest.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.dao.EmployeeDao;
import com.jgermaine.fyp.rest.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeDao EmployeeDao;
	
	
	public Employee getEmployee(){
		Employee employee = new Employee();
		employee.setEmail("jason@council.ie");
		employee.setPassword("password");
		return employee;
	}
	
	public void addEmployee(Employee employee) {
		EmployeeDao.create(employee);
	}
	
	public void removeEmployee(Employee employee) {
		EmployeeDao.delete(employee);
	}
	
	public List<Employee> getEmployees() {
		return EmployeeDao.getAll();
	}
	
	public List<Employee> getUnassignedEmployees() {
		return EmployeeDao.getUnassigned();
	}
	
	public Employee getEmployee(String email) {
		return EmployeeDao.getByEmail(email);
	}
	
} 