package com.jgermaine.fyp.rest.service.impl;

import java.util.HashMap;
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
	
	public void updateEmployee(Employee employee) {
		EmployeeDao.update(employee);
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

	@Override
	public HashMap<String, Long> getEmployeesStatistics() {
		HashMap<String, Long> statMap = new HashMap<String, Long>();
		
		long all = EmployeeDao.getAllCount();
		long unassigned = EmployeeDao.getUnassignedCount();
		
		statMap.put("emp_all", all);
		statMap.put("emp_unassigned", unassigned);
		statMap.put("emp_assigned", all - unassigned);
		
		return statMap;
	}
	
} 