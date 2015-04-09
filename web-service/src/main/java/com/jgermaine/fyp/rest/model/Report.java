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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "Reports")
@JsonIgnoreProperties(value={"employee", "citizen"})
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id", unique = true, nullable = false)
	private int id;
	
	@NotEmpty
	@Size(max = 80)
	@Pattern(regexp="[A-Za-z0-9?.$%, ]*")
	private String name;
	
	private double longitude;
	
	private double latitude;
	
	@NotNull
	private Date timestamp;
	
	private boolean status;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="report",cascade=CascadeType.PERSIST)
	private List<Entry> entries;
	
	@Transient
	private String employeeId;

	@Transient
	private String citizenId;
	
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="emp_email", nullable=true)
	private Employee employee;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "citz_email", nullable=false)
	private Citizen citizen;
    
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
    
    public Citizen getCitizen() {
    	return citizen;
    }
    
    public void setCitizen(Citizen citz) {
    	this.citizen = citz;
    }
    
    public String getEmployeeId() {
    	employeeId = null;
    	if (employee != null) {
    		employeeId = employee.getEmail();
    	}
    	return employeeId;
    }
    
    public String getCitizenId() {
    	if (citizen != null) {
    		citizenId = citizen.getEmail();
    	}
    	return citizenId;
    }
    
    public void setCitizenId(String citizenId) {
    	this.citizenId = citizenId;
    }
} 