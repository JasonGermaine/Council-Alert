package com.jgermaine.fyp.rest.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "employee")
public class Employee {
	
	private static int counter = 0;
	
	@Id
    @NotEmpty(message = "Please enter an email addresss.")
	@Email(message = "Please enter a valid email")
    private String email;
	
    @NotEmpty(message = "Please enter a password")
    //@Pattern(regexp="[a-zA-Z0-9]", message="Password does not match criteria")
    private String password;
    
    @NotEmpty(message="Please enter a first name")
    private String firstName;
    
    @NotEmpty(message="Please enter a last name")
    private String lastName;
    
    //@NotEmpty(message="Please enter a phone number")
    private String phoneNum;
    
    private String deviceId;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="id", nullable=true)
    private Report report;
    
    public Employee() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    
    public Report getReport() {
    	return report;
    }
    
    public void setReport(Report report) {
    	this.report = report;
    }
    
    @PrePersist
    void preInsert() {
       if (email == null) {
    	   email += "admin" + Integer.toString(counter);
    	   counter++;
       }
    }
}
