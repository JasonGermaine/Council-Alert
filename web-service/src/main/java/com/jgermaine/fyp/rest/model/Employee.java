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
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Employees")
@JsonIgnoreProperties(value={"report"})
public class Employee {
	
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
    
    private double longitude, latitude;
    
    private String deviceId;
    
    @OneToOne
    @JoinColumn(name="report_id", nullable=true)
    private Report report;
    
    @Transient
    private String reportId;
    
    public Employee() {

    }

    public String getReportId() {
    	String id = null;
    	if(report != null) {
    		id = Integer.toString(report.getId());
    	}
    	return id;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public Report getReport() {
    	return report;
    }
    
    public void setReport(Report report) {
    	this.report = report;
    }
    
    //public String getReportId() {
    //    return Integer.toString(report.getId());
    //}
    
    @Override
    public String toString() {
    	return String.format(
    			"Email: %s \tPassword: %s \nName: %s %s \nPhoneNumber: %s", 
    			getEmail(), getPassword(), getFirstName(), getLastName(), getPhoneNum());
    }
}
