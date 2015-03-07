package com.jgermaine.fyp.rest.gcm;

public class GcmOperations {

	public static void sendReportIdAsNotification(String id, String employeeDevice) {
		new Thread(new GCMRunnable(id, employeeDevice)).start();
	}
	
}
