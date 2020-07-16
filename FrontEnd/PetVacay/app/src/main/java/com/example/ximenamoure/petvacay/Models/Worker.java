package com.example.ximenamoure.petvacay.Models;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;


public class Worker {

    private int WorkerId;
    private byte [] ProfileImage;
    private String Name;
    private String Surname;
    private String ShortDescription;
    private int BoardingPrice;
    private int WalkingPrice;
    private byte [] BannerImage;
    private String PhoneNumber;
    private double Latitude;
    private double Longitude;
    private double AverageScore;
    private boolean Walker;

    public int GetWorkerId() {return WorkerId;}
    public byte [] GetProfileImage() {return ProfileImage;}
    public String GetName() {return Name;}
    public String GetSurname() {return Surname;}
    public String GetDescription() {return ShortDescription;}
    public int GetBoardingPrice() {return BoardingPrice;}
    public int GetWalkingPrice() {return WalkingPrice;}
    public byte [] GetBannerImage() {return BannerImage;}
    public String GetPhoneNumber() {return PhoneNumber;}
    public double GetLatitude() {return Latitude;}
    public double GetLongitude() {return Longitude;}
    public double GetAverageScore() {return AverageScore;}
    public boolean IsWalker() {return Walker;}

    public Worker(JSONObject json)
    {
        try{
            this.WorkerId = Integer.parseInt(json.get("workerId").toString());
            this.Name = json.get("name").toString();
            this.Surname = json.get("surname").toString();
            this.ShortDescription = json.get("shortDescription").toString();
            this.BoardingPrice = Integer.parseInt(json.get("boardingPrice").toString());
            this.WalkingPrice = Integer.parseInt(json.get("walkingPrice").toString());
            this.ProfileImage = getByteArray(json.get("profileImage").toString());
            this.BannerImage = getByteArray(json.get("bannerImage").toString());
            this.PhoneNumber = json.get("phoneNumber").toString();

            if(!json.getJSONObject("location").equals("null")){
                this.Latitude = Double.parseDouble(json.getJSONObject("location").get("latitude").toString());
                this.Longitude = Double.parseDouble(json.getJSONObject("location").get("longitude").toString());
            }

            this.AverageScore = json.getDouble("averageScore");
            this.Walker = json.getBoolean("isWalker");
        }
        catch (JSONException ex) {}

    }

    private byte[] getByteArray(String workerImage) {
        return Base64.decode(workerImage, Base64.DEFAULT);
    }
}
