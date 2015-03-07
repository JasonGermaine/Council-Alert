package com.jgermaine.fyp.rest.model;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "Employee")
@DiscriminatorValue(value = "Emp")
@JsonIgnoreProperties(value={"report", "deviceId"})
@AttributeOverride(name="email", column=@Column(name="emp_email"))
public class Employee extends User {

    @NotEmpty(message="Please enter a first name")
    private String firstName;
    
    @NotEmpty(message="Please enter a last name")
    private String lastName;
    
    //@NotEmpty(message="Please enter a phone number")
    private String phoneNum;
    
    private double longitude, latitude;
    
    private String deviceId;
    
    @OneToOne (mappedBy="employee", optional=true, cascade=CascadeType.ALL)
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

	@Override
	public String getType() {
		return "employee";
	}
}
