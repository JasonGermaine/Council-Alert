package com.jgermaine.fyp.rest.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


public class UserRequest {
	
    @NotEmpty
	@Email
	@Length(max = 255)
	private String email;
    
    @Length(max = 4000)
	private String deviceId;

    public UserRequest() {
        
    }
    
	public UserRequest(String email) {
		this.email = email;
	}
	
	public UserRequest(String email, String deviceId) {
		this(email);
		this.deviceId = deviceId;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}
