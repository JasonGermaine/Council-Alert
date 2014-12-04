package com.jgermaine.fyp.android_client.model;

import java.util.Date;

/**
 * Created by jason on 09/11/14.
 */
public class Report {
    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private Date timestamp;
    private boolean status;

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
}