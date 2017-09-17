package com.labs.tatu.runclub.model;

/**
 * Created by amush on 10-Sep-17.
 */

public class Location {
 private String locationName,locationGoal,locationPhotoUrl;
 private Long fromLat,fromLong,toLat,toLong;
 private int locationDistance;
    


    public Location(String locationName, int locationDistance, String locationGoal, String locationPhotoUrl, Long fromLat, Long fromLong, Long toLat, Long toLong) {
        this.locationName = locationName;
        this.locationDistance = locationDistance;
        this.locationGoal = locationGoal;
        this.locationPhotoUrl = locationPhotoUrl;
        this.fromLat = fromLat;
        this.fromLong = fromLong;
        this.toLat = toLat;
        this.toLong = toLong;
    }

    public Location()
    {

    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getLocationDistance() {
        return locationDistance;
    }

    public void setLocationDistance(int locationDistance) {
        this.locationDistance = locationDistance;
    }

    public String getLocationGoal() {
        return locationGoal;
    }

    public void setLocationGoal(String locationGoal) {
        this.locationGoal = locationGoal;
    }

    public String getLocationPhotoUrl() {
        return locationPhotoUrl;
    }

    public void setLocationPhotoUrl(String locationPhotoUrl) {
        this.locationPhotoUrl = locationPhotoUrl;
    }

    public Long getFromLat() {
        return fromLat;
    }

    public void setFromLat(Long fromLat) {
        this.fromLat = fromLat;
    }

    public Long getFromLong() {
        return fromLong;
    }

    public void setFromLong(Long fromLong) {
        this.fromLong = fromLong;
    }

    public Long getToLat() {
        return toLat;
    }

    public void setToLat(Long toLat) {
        this.toLat = toLat;
    }

    public Long getToLong() {
        return toLong;
    }

    public void setToLong(Long toLong) {
        this.toLong = toLong;
    }
}
