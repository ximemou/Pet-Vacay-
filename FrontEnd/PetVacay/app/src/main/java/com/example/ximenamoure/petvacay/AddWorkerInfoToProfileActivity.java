package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddWorkerInfoToProfileActivity extends AppCompatActivity implements LocationListener {

    static final int TOMAR_FOTO = 100;
    Uri fileUri;
    private ImageButton imageButton;
    private TextView workerName;
    private TextView workerSurname;
    private TextView workerPhone;
    private Button updateBtn;
    private JSONObject workerData;

    private LocationManager locationManager;

    private int workerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker_info_to_profile);

        workerId = getIntent().getIntExtra("WORKERID", 0);

        imageButton = (ImageButton) findViewById(R.id.changeImageWorker);
        workerName = (TextView) findViewById(R.id.WorkerName);
        workerSurname = (TextView) findViewById(R.id.WorkerSurname);
        workerPhone = (TextView) findViewById(R.id.WorkerPhone);
        updateBtn = (Button) findViewById(R.id.updateWorkerBtn);

        fillWorkerData();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            imageButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        //Para API 23+ para que no tire FileUriExposedException
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getNewFile();
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, TOMAR_FOTO);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateWorkerData();
            }
        });


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        updateLocation();
    }

    private void updateLocation(){
        if (ContextCompat.checkSelfPermission(AddWorkerInfoToProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(AddWorkerInfoToProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddWorkerInfoToProfileActivity.this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 2);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 30, 0, this);
            //llama al onLocationChanged
        }
    }

    private void fillWorkerData() {
        try{
            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("ResponseWorker", "");
            workerData = new JSONObject(response);

            if(!workerData.get("name").toString().equals("null")){
                workerName.setText(workerData.get("name").toString());
                workerSurname.setText(workerData.get("surname").toString());
                workerPhone.setText(workerData.get("phoneNumber").toString());
            }
            else{
                workerName.setText("EjemploNombre");
                workerSurname.setText("EjemploApellido");
                workerPhone.setText("000000000");
            }

        }
        catch (JSONException ex){}
    }

    private Uri getNewFile(){
        Uri uri = null;
        try{
            //it creates a folder  misFotos in the directory of the photos
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "misFotos");
            if(!mediaStorageDir.exists()){
                mediaStorageDir.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            uri = Uri.fromFile(mediaFile);
        }
        catch(Exception ex){
            Log.e("Error al crear archivo", ex.getMessage());
        }
        return uri;
    }

    @Override //called when  startActivityForResult returns a value
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try{
            if(requestCode == TOMAR_FOTO){
                if(resultCode == RESULT_OK){

                    File imgFile = new File(fileUri.getPath());

                    SendImage(imgFile);
                }
            }
        }
        catch(Exception ex){
            Log.e("Error al volver de foto", ex.getMessage());
        }
    }

    private void SendImage(File imgFile) {

        String url = BackendConfig.GetInstance().BackendIP + "workers/" + workerId;
        byte [] image = getImageByteArray(imgFile);
        JSONObject jsonToSend = makeJson(image);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AddWorkerInfoToProfileActivity.this, "Imagen de perfil actualizada.", Toast.LENGTH_LONG).show();
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

                                Toast.makeText(AddWorkerInfoToProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private byte[] getImageByteArray(File imgFile) {

        int size = (int) imgFile.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(imgFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
        b.compress(Bitmap.CompressFormat.JPEG, 15, byteOutput);
        bytes = byteOutput.toByteArray();
        return bytes;
    }

    private JSONObject makeJson(byte[] image) {
        JSONObject json = new JSONObject();
        try{
            json.put("Name", workerName.getText().toString());
            json.put("Surname", workerSurname.getText().toString());
            json.put("PhoneNumber", workerPhone.getText().toString());
            json.put("Password", workerData.get("password").toString());

            json.put("ProfileImage", new JSONArray(image));
        }
        catch(JSONException ex) {}
        return json;
    }

    private JSONObject makeJsonUpdate(){
        JSONObject json = new JSONObject();
        try{
            json.put("Name", workerName.getText().toString());
            json.put("Surname", workerSurname.getText().toString());
            json.put("PhoneNumber", workerPhone.getText().toString());
            json.put("Password", workerData.get("password").toString());
        }
        catch(JSONException ex) {}
        return json;
    }

    private void UpdateWorkerData() {

        String url = BackendConfig.GetInstance().BackendIP + "workers/" + workerId;
        JSONObject jsonToSend = makeJsonUpdate();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Intent i = new Intent(AddWorkerInfoToProfileActivity.this, WorkerProfileActivity.class);
                        i.putExtra("WORKER_ID", workerId);
                        startActivity(i);
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

                                Toast.makeText(AddWorkerInfoToProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imageButton.setEnabled(true);
            }
        }
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

                                Toast.makeText(AddWorkerInfoToProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
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
