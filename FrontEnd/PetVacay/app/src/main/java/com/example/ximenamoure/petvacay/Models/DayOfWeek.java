package com.example.ximenamoure.petvacay.Models;

import org.json.JSONException;
import org.json.JSONObject;


public class DayOfWeek {
    private String name;
    private boolean checked;

    public String GeWeekDayName(){
        return name;
    }

    public boolean IsChecked() { return checked; }

    public void SetChecked(boolean newChecked) { checked = newChecked; }

    public DayOfWeek(String dName, boolean isChecked)
    {
        name = dName;
        checked = isChecked;
    }
}
