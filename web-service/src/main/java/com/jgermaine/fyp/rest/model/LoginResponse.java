package com.jgermaine.fyp.rest.model;

public class LoginResponse {

	private String type;
	private User user;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}
