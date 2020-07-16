package com.example.ximenamoure.petvacay.Models;

import org.json.JSONException;
import org.json.JSONObject;


public class Inform {

    private int InformId;
    private int BookingId;
    private String InformDate;
    private String InformData;

    public int GetInformId() {return InformId;}
    public int GetBookingId(){return BookingId;}
    public String GetInformDate(){return InformDate;}
    public String GetInformData(){return InformData;}

    public Inform() {}

    public Inform(JSONObject json){
        try{
            InformId = json.getInt("informId");
            BookingId = json.getInt("bookingId");
            InformDate = json.getString("dateOfInform");
            InformData = json.getString("informData");
        }
        catch(JSONException ex){}
    }
}
