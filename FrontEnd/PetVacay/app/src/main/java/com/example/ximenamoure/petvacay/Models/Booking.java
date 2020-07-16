package com.example.ximenamoure.petvacay.Models;

import org.json.JSONException;
import org.json.JSONObject;



public class Booking {

    private int BookingId;
    private String BookingStartDate;
    private String BookingFinishDate;
    private boolean WalkingType;
    private boolean WalkingStarted;
    private int WorkerId;

    public int GetBookingId(){return BookingId;}
    public String GetStartDay(){return BookingStartDate;}
    public String GetFinishDay(){return BookingFinishDate;}
    public boolean IsWalkingType() {return WalkingType;}
    public boolean WalkingStarted() {return WalkingStarted;}
    public int GetWorkerId() {return WorkerId;}

    public Booking() {}

    public Booking(JSONObject json){
        try{
            BookingId = json.getInt("bookingId");
            BookingStartDate = json.getString("startDate");
            BookingFinishDate = json.getString("finishDate");
            WalkingType = json.getBoolean("isWalker");
            WalkingStarted = json.getBoolean("walkingStarted");
            WorkerId = json.getInt("workerId");
        }
        catch(JSONException ex){}
    }
}
