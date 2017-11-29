package com.labs.tatu.runforce.model;

/**
 * Created by amush on 18-Sep-17.
 */

public class Award {
    private String awardName,awardDescription,awardImage,awardPoints;


    public Award()
    {

    }

    public Award(String awardName, String awardDescription, String awardImage, String awardPoints) {
        this.awardName = awardName;
        this.awardDescription = awardDescription;
        this.awardImage = awardImage;
        this.awardPoints = awardPoints;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public String getAwardDescription() {
        return awardDescription;
    }

    public void setAwardDescription(String awardDescription) {
        this.awardDescription = awardDescription;
    }

    public String getAwardImage() {
        return awardImage;
    }

    public void setAwardImage(String awardImage) {
        this.awardImage = awardImage;
    }

    public String getAwardPoints() {
        return awardPoints;
    }

    public void setAwardPoints(String awardPoints) {
        this.awardPoints = awardPoints;
    }
}
