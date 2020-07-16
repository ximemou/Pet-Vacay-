package com.example.ximenamoure.petvacay.Models;

import android.util.Base64;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class Pet {

    private String PetName;
    private String PetType;
    public byte [] PetImage;

    private int petId;

    public String GetPetName(){
        return PetName;
    }

    public String GetPetType(){
        return PetType;
    }

    public byte [] GetPetImage(){
        return PetImage;
    }

    public int GetPetId(){return petId;}

    public Pet(String name, String type, byte [] image,int id)
    {
        PetName = name;
        PetType = type;
        PetImage = image;
        petId=id;
    }

    public Pet(JSONObject json)
    {
        try{
            this.PetName = json.get("name").toString();
            this.PetType = json.get("petType").toString();
            this.petId=(int)json.get("petId");
            this.PetImage = getByteArray(json.get("petImage").toString());
        }
        catch (JSONException ex) {}

    }

    private byte[] getByteArray(String petImage) {
       return Base64.decode(petImage, Base64.DEFAULT);
    }
}
