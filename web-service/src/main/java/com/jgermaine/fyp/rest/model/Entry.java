package com.jgermaine.fyp.rest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Entries")
@JsonIgnoreProperties(value={"imageString", "report"})
public class Entry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "entry_id", unique = true, nullable = false)
	private int id;

	private Date timestamp;
	
	@Lob
	@Column(columnDefinition = "mediumblob")
	private byte[] image;
	
	@Length(max = 255)
	private String comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable=false)
	private Report report;

    @NotEmpty
	@Email
	@Length(max = 255)
	private String author;
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	
    public Report getReport() {
    	return report;
    }
    
    public void setReport(Report report) {
    	this.report = report;
    }
    
    public String getAuthor() {
    	return this.author;
    }
    
    public void setAuthor(String author) {
    	this.author = author;
    }
}
