package com.jgermaine.fyp.android_client.model;

import java.util.Date;

/**
 * Created by jason on 09/11/14.
 */
public class Report {
    private int id;
    private String name, description, comment;
    private double longitude, latitude;
    private Date timestamp;
    private boolean status;
    private byte[] imageBefore, imageAfter;

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

    public boolean getStatus() { return status; }

    public void setStatus(boolean status) { this.status = status; }

    public byte[] getImageBefore() { return imageBefore; }

    public void setImageBefore(byte[] image) { this.imageBefore = image; }

    public byte[] getImageAfter() { return imageAfter; }

    public void setImageAfter(byte[] image) { this.imageAfter = image; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}