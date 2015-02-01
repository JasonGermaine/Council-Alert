package com.jgermaine.fyp.rest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "report")
@JsonIgnoreProperties(value={"imageBeforeUrl", "imageAfterUrl", "employee"})
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotNull
	@Size(max = 80)
	private String name;
	
	private String description;
	private String comment;
	private double longitude, latitude;
	private Date timestamp;
	private boolean status;
	
	@Lob
	@Column(columnDefinition = "mediumblob")
	private byte[] imageBefore;

	@Lob
	@Column(columnDefinition = "mediumblob")
	private byte[] imageAfter;
	
	@Transient
	private String imageBeforeUrl;
	
	@Transient
	private String imageAfterUrl;
	
	@OneToOne (mappedBy="report")
	private Employee employee;
	
	public Report() { 
		
	}
	 
	public Report(int id) { 
	    this.id = id;
	}
	  
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    
    public boolean getStatus() {
    	return status;
    }
    
    public void setStatus(boolean status) {
    	this.status = status;
    }
    
    public byte[] getImageBefore() {
    	return imageBefore;
    }
    
    public void setImageBefore(byte[] image) {
    	this.imageBefore = image;
    }
    
    public byte[] getImageAfter() {
    	return imageAfter;
    }
    
    public void setImageAfter(byte[] image) {
    	this.imageAfter = image;
    }
    
    public String getImageBeforeUrl() {
    	String url = new String(Base64.encodeBase64(imageBefore));
    	imageBeforeUrl = "data:image/png;base64," + url;
    	return imageBeforeUrl;
    }
    
    public String getImageAfterUrl() {
    	String url = new String(Base64.encodeBase64(imageAfter));
    	imageAfterUrl = "data:image/png;base64," + url;
    	return imageAfterUrl;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public void setDescription(String description) {
    	this.description = description;
    }
    
    public String getComment() {
    	return comment;
    }
    
    public void setComment(String comment) {
    	this.comment = comment;
    }
    
    public Employee getEmployee() {
    	return employee;
    }
    
    public void setEmployee(Employee emp) {
    	this.employee = emp;
    }
} 