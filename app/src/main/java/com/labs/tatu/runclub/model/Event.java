package com.labs.tatu.runclub.model;

/**
 * Created by amush on 14-Sep-17.
 */

public class Event {
    private String eventName,eventDate,eventTime,eventLocation,eventType,eventFee,eventDesc,eventImage,eventAttending;



    public Event(String eventName, String eventDate, String eventTime, String eventLocation, String eventType, String eventFee, String eventDesc, String eventImage, String eventAttending) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventType = eventType;
        this.eventFee = eventFee;
        this.eventDesc = eventDesc;
        this.eventImage = eventImage;
        this.eventAttending = eventAttending;
    }
    public Event()
    {

    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventFee() {
        return eventFee;
    }

    public void setEventFee(String eventFee) {
        this.eventFee = eventFee;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventAttending() {
        return eventAttending;
    }

    public void setEventAttending(String eventAttending) {
        this.eventAttending = eventAttending;
    }
}
