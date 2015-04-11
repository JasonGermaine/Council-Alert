package com.jgermaine.fyp.rest.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message.Builder;

public class GCMRunnable implements Runnable {

	private static final Logger LOGGER = LogManager
			.getLogger(GCMRunnable.class.getName());
	
    private String id, employeeDevice;

    public GCMRunnable(String id, String employeeDevice) {
        this.id = id;
        this.employeeDevice = employeeDevice;
    }

    public void run() {
    	try {
	    	Map<String,String> data = new HashMap<String,String>();
		    data.put("reportId", id);
			
		    // Sender API Key
		    Sender sender = new Sender("AIzaSyCFZkkdRYr-xnhmk_wbvVPAX6eu6pVc4eU");
			Builder b = new Builder();
			
			// Set data
			b.setData(data);
			Message message = b.build();
			
			// Add Android device key
			String devices = employeeDevice;
			
			
			Result response = sender.send(message, devices, 5);
			LOGGER.info("Sent report " + id + " to device " + employeeDevice);
		} catch (IOException e) {
			LOGGER.error("Google Cloud Messaging Failure", e);
		}		
    }
}
