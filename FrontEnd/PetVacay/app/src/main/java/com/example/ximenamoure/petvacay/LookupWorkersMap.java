package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.Worker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LookupWorkersMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_workers_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getWorkersFromDB();
    }

    private void getWorkersFromDB() {
        String url = BackendConfig.GetInstance().BackendIP + "workers";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        populateMapWithWorkers(response);
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

                                Toast.makeText(LookupWorkersMap.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void populateMapWithWorkers(JSONArray json) {
        if (ContextCompat.checkSelfPermission(LookupWorkersMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LookupWorkersMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LookupWorkersMap.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            ArrayList<Worker> workers = convertToArrayOfWorkers(json);

            for (Worker w : workers) {
                LatLng workerLocation = new LatLng(w.GetLatitude(), w.GetLongitude());
                String markerTitle = w.GetName() + " " + w.GetSurname();
                Marker m = mMap.addMarker(new MarkerOptions().position(workerLocation).title(markerTitle));
                m.setTag(new Object[] {w.GetWorkerId(), w.GetPhoneNumber(),w.GetBoardingPrice(),w.GetWalkingPrice()});

                if(w.IsWalker()){
                    m.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("walker",100,100)));
                }
                else{
                    m.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("boarder",100,100)));
                }

                m.showInfoWindow();
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getFirstLocation(workers), 17.0f));
            mMap.setOnMarkerClickListener(this);
        }
    }

    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private LatLng getFirstLocation(ArrayList<Worker> workers) {
        double lat = workers.get(0).GetLatitude();
        double lngt = workers.get(0).GetLongitude();
        LatLng latLng = new LatLng(lat, lngt);
        return latLng;
    }

    private ArrayList<Worker> convertToArrayOfWorkers(JSONArray jsonArray) {
        ArrayList<Worker> workers = new ArrayList<Worker>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Worker convertedWorker = new Worker(jsonArray.getJSONObject(i));
                workers.add(convertedWorker);
            }
        } catch (JSONException ex) {
        }
        return workers;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        Object [] data = (Object []) marker.getTag();
        int workerId = (Integer) data[0];
        String workerPhone = (String) data[1];
        int workerBoardingPrice=(Integer)data[2];
        int workerWalkingPrice=(Integer)data[3];

        Intent i = new Intent(LookupWorkersMap.this, BookWorkerActivity.class);
        i.putExtra("SelectedWorker", workerId);
        i.putExtra("SelectedWorkerPhone", workerPhone );
        i.putExtra("WalkingPrice",workerWalkingPrice);
        i.putExtra("BoardingPrice",workerBoardingPrice);
        startActivity(i);
        return false;
    }
}
