package com.jgermaine.fyp.rest.model;

import java.util.Date;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.apache.commons.codec.binary.Base64;

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
	private String comment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_id", nullable=false)
	private Report report;
	
	@Transient
	private String imageString;
	
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
	
    public String getImageString() {
    	String url = new String(Base64.encodeBase64(image));
    	imageString = "data:image/png;base64," + url;
    	return imageString;
    }
    
    public Report getReport() {
    	return report;
    }
    
    public void setReport(Report report) {
    	this.report = report;
    }
}
