package com.jgermaine.fyp.rest.task;

import com.jgermaine.fyp.rest.model.Employee;

public class TaskManager {

	/**
	 * Decides whether a notification should be sent to an employee by email or
	 * device notification
	 * 
	 * @param id
	 * @param employee
	 */
	public static void sendReportIdAsNotification(String id, Employee employee) {
		if (employee.getDeviceId() != null && !employee.getDeviceId().isEmpty()) {
			new Thread(new GCMRunnable(id, employee.getDeviceId())).start();
		} else {
			new Thread(new MailRunnable(id, employee)).start();
		}
	}

}
