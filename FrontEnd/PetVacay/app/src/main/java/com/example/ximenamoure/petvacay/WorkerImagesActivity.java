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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
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
import com.example.ximenamoure.petvacay.Adapters.WorkerImageListAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.WorkerImage;
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
import java.util.Iterator;

public class WorkerImagesActivity extends AppCompatActivity implements LocationListener{

    private int workerId;
    JSONArray workerImages;

    private RecyclerView mRecycleView;
    private GridLayoutManager mGridLayoutManager;
    private WorkerImageListAdapter mAdapter;
    private ArrayList<WorkerImage> mImageList;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_images);

        workerId = getIntent().getIntExtra("WorkerID", 0);

        getWorkerImages();

        mRecycleView = (RecyclerView) findViewById(R.id.workerImagesContainer);
        mRecycleView.setNestedScrollingEnabled(false);

        int numberOfColumns = 2;
        mGridLayoutManager = new GridLayoutManager(WorkerImagesActivity.this, numberOfColumns);
        mRecycleView.setLayoutManager(mGridLayoutManager);

        mImageList = new ArrayList<>();
        mAdapter = new WorkerImageListAdapter(WorkerImagesActivity.this, mImageList);
        mRecycleView.setAdapter(mAdapter);

        setNavigationDrawer();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        updateLocation();
    }

    private void updateLocation(){
        if (ContextCompat.checkSelfPermission(WorkerImagesActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(WorkerImagesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WorkerImagesActivity.this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 2);
        }
        else{
            locationManager.requestLocationUpdates(BackendConfig.GetInstance().LocationProvider, 1000 * 30, 0, this);
            //calls  onLocationChanged
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
                                i = new Intent(WorkerImagesActivity.this, WorkerProfileActivity.class);
                                i.putExtra("WORKER_ID", workerId);
                                startActivity(i);
                                break;
                            case 2:
                                i = new Intent(WorkerImagesActivity.this, LoginActivity.class);
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

    private void getWorkerImages(){
        String url = BackendConfig.GetInstance().BackendIP + "workers/" + workerId + "/images";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        sharedResponse(response);
                        fillImages();
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

                                Toast.makeText(WorkerImagesActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void sharedResponse(JSONArray response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("ResponseWorkerImages", response.toString());
        editor.commit();
    }

    private void fillImages(){
        try{
            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("ResponseWorkerImages", "");
            workerImages = new JSONArray(response);

            ArrayList<WorkerImage> images = convertToArrayOfImages(workerImages);
            mImageList.addAll(images);
            mAdapter.notifyItemInserted(mImageList.size());

        }
        catch (JSONException ex) {}
    }

    private ArrayList<WorkerImage> convertToArrayOfImages(JSONArray jsonArray)
    {
        ArrayList<WorkerImage> imgs = new ArrayList<WorkerImage>();
        try{
            for(int i = 0; i<jsonArray.length(); i++)
            {
                WorkerImage convertedImage = new WorkerImage(jsonArray.getJSONObject(i));
                imgs.add(convertedImage);
            }
        }
        catch (JSONException ex) {}
        return imgs;
    }

    @Override
    public void onLocationChanged(Location location) {
        JSONObject json = makeJsonToSend(location);
        updateWorkerLocation(json);
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

    public void updateWorkerLocation(JSONObject dataToSend)
    {
        String url = BackendConfig.GetInstance().BackendIP + "workers/" + workerId + "/location";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.data != null)
                        {
                            try{
                                String msg = new String(error.networkResponse.data, "UTF-8");
                                JSONObject json;
                                try{
                                    json = new JSONObject(msg);
                                    msg = json.getString("message");
                                }
                                catch(JSONException ex) {}

                                Toast.makeText(WorkerImagesActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private JSONObject makeJsonToSend(Location location){
        JSONObject json = new JSONObject();
        try{
            String latitude = Double.toString(location.getLatitude());
            String longitude = Double.toString(location.getLongitude());
            json.put("Latitude", latitude);
            json.put("Longitude", longitude);
        }
        catch(JSONException ex){}
        return json;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }
}
