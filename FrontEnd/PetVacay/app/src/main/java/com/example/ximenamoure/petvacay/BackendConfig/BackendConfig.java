package com.example.ximenamoure.petvacay.BackendConfig;


import android.location.LocationManager;

public class BackendConfig {

    private static BackendConfig config;

    public static String BackendIP = "http://172.29.2.15:8091/api/";

    public static String LocationProvider= LocationManager.NETWORK_PROVIDER;

    private BackendConfig() {}

    public static BackendConfig GetInstance()
    {
        if(config == null)
        {
            return new BackendConfig();
        }
        return config;
    }
}
