package com.example.ximenamoure.petvacay.Models;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;


public class WorkerImage {

    private byte [] Image;

    public byte[] GetImage(){
        return Image;
    }

    public WorkerImage(byte[] img){
        Image = img;
    }

    public WorkerImage(JSONObject json)
    {
        try{
            Image = getByteArray(json.get("image").toString());
        }
        catch (JSONException ex) {}

    }

    private byte[] getByteArray(String workerImage) {
        return Base64.decode(workerImage, Base64.DEFAULT);
    }
}
