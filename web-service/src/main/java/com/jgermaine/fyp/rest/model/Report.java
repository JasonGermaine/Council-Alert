package com.jgermaine.fyp.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "Reports")
@JsonIgnoreProperties(value={"employee"})
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id", unique = true, nullable = false)
	private int id;
	
	@NotNull
	@Size(max = 80)
	private String name;
	
	private double longitude, latitude;
	private Date timestamp;
	private boolean status;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="report",cascade=CascadeType.ALL)
	private List<Entry> entries;
	
	@Transient
	private String employeeId;
	
	@OneToOne (mappedBy="report", optional=true,  fetch = FetchType.LAZY)
	private Employee employee;
	
	public List<Entry> getEntries() {
		return this.entries;
	}
	
	public  void setEntries(List<Entry> entries) {
		for(Entry entry : entries) {
			addEntry(entry);
		}
	}
	
	public void addEntry(Entry entry) {
		if (entries == null) {
			entries = new ArrayList<Entry>();
		}
		entry.setReport(this);
		entries.add(entry);
	}
	
	public Report() { 
		employee = null;
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
    
    public Employee getEmployee() {
    	return employee;
    }
    
    public void setEmployee(Employee emp) {
    	this.employee = emp;
    }
    
    public String getEmployeeId() {
    	String id = null;
    	if (employee != null) {
    		id = employee.getEmail();
    	}
    	return id;
    }    
    
} 