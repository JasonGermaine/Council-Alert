package com.jgermaine.fyp.rest.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "report")
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotNull
	@Size(max = 80)
	private String name;
	
	
	public Report() { 
		
	}
	 
	public Report(int id) { 
	    this.id = id;
	}
	
	public Report(int id, String name) {
		this.id = id;
		this.name = name;
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

} 