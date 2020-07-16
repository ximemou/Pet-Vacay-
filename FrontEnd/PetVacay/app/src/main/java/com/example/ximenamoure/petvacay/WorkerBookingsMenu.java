package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.Adapters.WorkerBookingsAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.Booking;
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

public class WorkerBookingsMenu extends AppCompatActivity implements LocationListener{

    private int workerId;

    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private WorkerBookingsAdapter mAdapter;
    private ArrayList<Booking> mBookingList;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_bookings_menu);

        mRecycleView = (RecyclerView) findViewById(R.id.workerBookingsContainer);
        mRecycleView.setNestedScrollingEnabled(false);

        mLinearLayoutManager = new LinearLayoutManager(WorkerBookingsMenu.this);
        mRecycleView.setLayoutManager(mLinearLayoutManager);

        mBookingList = new ArrayList<>();
        mAdapter = new WorkerBookingsAdapter(WorkerBookingsMenu.this, mBookingList);
        mRecycleView.setAdapter(mAdapter);

        workerId = getLoggedWorker();

        setNavigationDrawer();
        showBookings();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        updateLocation();
    }

    private void updateLocation(){
        if (ContextCompat.checkSelfPermission(WorkerBookingsMenu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(WorkerBookingsMenu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WorkerBookingsMenu.this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 2);
        }
        else{
            locationManager.requestLocationUpdates(BackendConfig.GetInstance().LocationProvider, 10, 0, this);
        }
    }

    private void setNavigationDrawer(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Mi perfil");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("LogOut");

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        Intent i;
                        switch((int) drawerItem.getIdentifier()){
                            case 1:
                                i = new Intent(WorkerBookingsMenu.this, WorkerProfileActivity.class);
                                i.putExtra("WORKER_ID", workerId);
                                startActivity(i);
                                break;
                            case 2:
                                i = new Intent(WorkerBookingsMenu.this, LoginActivity.class);
                                startActivity(i);
                                break;
                        };
                        return true;
                    }
                })
                .build();
    }

    private int getLoggedWorker(){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(m.getString("LoggedWorker", ""));
    }

    private void showBookings(){
        String url = BackendConfig.GetInstance().BackendIP + "workers/"+ workerId + "/bookings";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        fillList(response);
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

                                Toast.makeText(WorkerBookingsMenu.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void fillList(JSONArray json){
        ArrayList<Booking> bookings = convertToArrayOfBookings(json);
        mBookingList.addAll(bookings);
        mAdapter.notifyItemInserted(mBookingList.size());
    }

    private ArrayList<Booking> convertToArrayOfBookings(JSONArray jsonArray){
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Booking convertedBooking = new Booking(jsonArray.getJSONObject(i));
                bookings.add(convertedBooking);
            }
        } catch (JSONException ex) {
        }
        return bookings;
    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("WorkerLocationLatitude", Double.toString(location.getLatitude()));
        editor.putString("WorkerLocationLongitude", Double.toString(location.getLongitude()));
        editor.commit();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
