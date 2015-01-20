package com.jgermaine.fyp.rest.gcm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GcmOperations {
	
	private static final Logger LOGGER = LogManager
			.getLogger(GcmOperations.class.getName());
	
	
	/**
	 * Sends dummy notification to dummy client
	 * @throws IOException
	 */
	public static void sendNotifcation() throws IOException {  
	    
		Map<String,String> data = new HashMap<String,String>();
	    data.put("title", "Pot Hole");
		
	    // Sender API Key
	    Sender sender = new Sender("AIzaSyCFZkkdRYr-xnhmk_wbvVPAX6eu6pVc4eU");
		Builder b = new Builder();
		
		// Set data
		b.setData(data);
		Message message = b.build();
		
		// Add Android device key
		String devices ="APA91bEV76JLm4l4KETI7hgefz6lcfeqWJ_oYGUC5WG_"
				+ "-Dvvs1t--eaBVKPTx9hMCLd3dAPeq7BafgqkFrJyNX1BVn1X-"
				+ "7ECId4BKPP9say2-ztQXuoyXQTwxRNsGzbOndfwJ-"
				+ "bJcyYO51XqflzAp0Bv0RMWmP5HvJe3Hav1w1daKlJTDQoIgAg";
		
		Result response = sender.send(message, devices, 5);
		LOGGER.debug("Sent message using Google Cloud Messaging");
		// TODO: Manage response
	}
}
