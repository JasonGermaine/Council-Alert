package com.jgermaine.fyp.rest.gcm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GcmOperations {
	
	public static void sendNotifcation() throws IOException {  
	    Map<String,String> data = new HashMap<String,String>();
	    data.put("title", "Hello-World");
		Sender sender = new Sender("AIzaSyCFZkkdRYr-xnhmk_wbvVPAX6eu6pVc4eU");
		Builder b = new Builder();
		b.setData(data);
		Message message = b.build();
		String devices ="APA91bEV76JLm4l4KETI7hgefz6lcfeqWJ_oYGUC5WG_"
				+ "-Dvvs1t--eaBVKPTx9hMCLd3dAPeq7BafgqkFrJyNX1BVn1X-"
				+ "7ECId4BKPP9say2-ztQXuoyXQTwxRNsGzbOndfwJ-"
				+ "bJcyYO51XqflzAp0Bv0RMWmP5HvJe3Hav1w1daKlJTDQoIgAg";
		
		Result result = sender.send(message, devices, 5);	              
	}
}
