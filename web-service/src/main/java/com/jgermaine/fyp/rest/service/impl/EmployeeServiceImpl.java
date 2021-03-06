package com.jgermaine.fyp.rest.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.dao.EmployeeDao;
import com.jgermaine.fyp.rest.service.EmployeeService;
import com.jgermaine.fyp.rest.task.TaskManager;

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
	
	public void updateEmployee(Employee employee) throws Exception {
		EmployeeDao.update(employee);
	}
	
	public void removeEmployee(Employee employee) throws Exception {
		EmployeeDao.delete(employee);
	}
	
	public List<Employee> getEmployees(int index) throws Exception {
		return EmployeeDao.getAll(index);
	}
	
	public List<Employee> getUnassignedEmployees(int index) throws Exception {
		return EmployeeDao.getUnassigned(index);
	}
	
	public List<Employee> getAssignedEmployees(int index) {
		return EmployeeDao.getAssigned(index);
	}
	
	public List<Employee> getUnassignedNearEmployees(double lat, double lon) throws Exception {
		return EmployeeDao.getUnassignedNearestEmployee(lat, lon);
	}
	
	public Employee getEmployee(String email) throws NoResultException, NonUniqueResultException, Exception {
		return EmployeeDao.getByEmail(email);
	}

	@Override
	public HashMap<String, Long> getEmployeesStatistics() throws Exception {
		HashMap<String, Long> statMap = new HashMap<String, Long>();
		
		long all = EmployeeDao.getAllCount();
		long unassigned = EmployeeDao.getUnassignedCount();
		long assigned = 0;
		
		statMap.put("emp_all", all);
		statMap.put("emp_unassigned", unassigned);
		
		if ((all - unassigned) > 0) {
			assigned = all - unassigned;
		}
		
		statMap.put("emp_assigned", assigned);
		return statMap;
	}
	
	public void sendEmployeeNotification(Employee emp, int reportId) {
		// Send notification regardless if DB needs updating
		// New Thread spawned for GCM so it does no block response
		TaskManager.sendReportIdAsNotification(Integer.toString(reportId), emp);
	}
} 