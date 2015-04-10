package com.jgermaine.fyp.rest.model;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Employee")
@DiscriminatorValue(value = "Emp")
@JsonIgnoreProperties(value={"report"})
@AttributeOverride(name="email", column=@Column(name="emp_email"))
public class Employee extends User {

    @NotEmpty
    @Length(max = 80)
    @Pattern(regexp="[A-Za-z]*")
    private String firstName;
    
    @NotEmpty
    @Length(max = 80)
    @Pattern(regexp="[A-Za-z]*")
    private String lastName;
    
    @NotEmpty
    @Length(max = 15)
    @Pattern(regexp="[0-9+-]*")
    private String phoneNum;
    
    private double longitude;
    
    private double latitude;
    
    @Length(max = 4000)
    private String deviceId;
    
    @OneToOne (mappedBy="employee", optional=true, cascade=CascadeType.ALL)
    private Report report;
    
    @Transient
    private String reportId;

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
