package com.jgermaine.fyp.rest.gcm;

import com.jgermaine.fyp.rest.model.Employee;

public class TaskManager {

	public static void sendReportIdAsNotification(String id, Employee employee) {
		if (employee.getDeviceId() != null && !employee.getDeviceId().isEmpty()) {
			new Thread(new GCMRunnable(id, employee.getDeviceId())).start();
		} else {
			new Thread(new MailRunnable(id, employee)).start();
		}
	}
	
}
