package com.jgermaine.fyp.rest.model;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

public class PasswordChangeRequest {

	@Pattern(regexp="[A-Za-z0-9?.$%]*")
	@Length(max = 255)
	private String password;
	
	@Pattern(regexp="[A-Za-z0-9?.$%]*")
	@Length(max = 255)
	private String newPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
