package com.example.ximenamoure.petvacay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class WalkingPetMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking_pet_map);
        bookingId = getIntent().getIntExtra("BOOKING_ID", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getBookingWorkerLocations();
    }

    private void getBookingWorkerLocations(){
        String url = BackendConfig.GetInstance().BackendIP + "bookings/" + bookingId + "/workerLocations";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        drawLineInMap(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.data != null) {
                            try {
                                String msg = new String(error.networkResponse.data, "UTF-8");
                                JSONObject json;
                                try {
                                    json = new JSONObject(msg);
                                    msg = json.getString("message");
                                } catch (JSONException ex) {
                                }

                                Toast.makeText(WalkingPetMap.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void drawLineInMap(JSONObject json){
        try{
            double initialLatitude = Double.parseDouble(json.getString("initialLatitude"));
            double initialLongitude = Double.parseDouble(json.getString("initialLongitude"));
            double currentLatitude = Double.parseDouble(json.getString("currentLatitude"));
            double currentLongitude = Double.parseDouble(json.getString("currentLongitude"));

            LatLng initialWalkingLocation = new LatLng(initialLatitude, initialLongitude);
            LatLng currentWorkerLocation = new LatLng(currentLatitude, currentLongitude);

            Marker mInitial = mMap.addMarker(new MarkerOptions().position(initialWalkingLocation).title("Comienzo"));
            mInitial.showInfoWindow();
            Marker mCurrent = mMap.addMarker(new MarkerOptions().position(currentWorkerLocation).title("Actual"));
            mCurrent.showInfoWindow();

            PolylineOptions newRoute = (getLastRoute(initialWalkingLocation, currentWorkerLocation))
                    .add(currentWorkerLocation).width(5).color(Color.RED);
            mMap.addPolyline(newRoute);
            saveNewRoute(newRoute);

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialWalkingLocation, 17.0f));
        }
        catch (JSONException ex){}
    }

    private PolylineOptions getLastRoute(LatLng initial, LatLng current){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        String coordinates = m.getString("ArrayRouteBooking" + bookingId, "");
        PolylineOptions polyline = new PolylineOptions();

        try{
            if(coordinates.length() > 0){
                JSONArray json = new JSONArray(coordinates);

                for(int i = 0; i<json.length() - 1; i=i+2){
                    LatLng l = new LatLng(json.getDouble(i), json.getDouble(i+1));
                    polyline.add(l);
                }
            }
            else{
                polyline = new PolylineOptions().add(initial, current).width(5).color(Color.RED);
            }
        }
        catch (JSONException ex){}
        return polyline;
    }

    private void saveNewRoute(PolylineOptions route){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        List<LatLng> points = route.getPoints();
        List<Double> locationsInPlainList = getInSingleList(route.getPoints());

        JSONArray json = new JSONArray(locationsInPlainList);
        editor.putString("ArrayRouteBooking" + bookingId, json.toString());
        editor.commit();
    }

    private List<Double> getInSingleList(List<LatLng> points) {
        List<Double> locations = new ArrayList<Double>();
        for(LatLng l : points){
            locations.add(l.latitude);
            locations.add(l.longitude);
        }
        return locations;
    }

}
