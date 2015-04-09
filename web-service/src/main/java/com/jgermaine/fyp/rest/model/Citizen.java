package com.jgermaine.fyp.rest.model;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Citizen")
@DiscriminatorValue(value = "Citz")
@AttributeOverride(name="email", column=@Column(name="citz_email"))
@JsonIgnoreProperties(value={"reports"})
public class Citizen extends User {

	@OneToMany(fetch = FetchType.LAZY, mappedBy="citizen",cascade=CascadeType.ALL)
	private List<Report> reports;

	@Override
	public String getType() {
		return "citizen";
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	
	public List<Report> getReports() {
		return reports;
	}
	
	public void addReport(Report report) {
		reports.add(report);		
	}
}
