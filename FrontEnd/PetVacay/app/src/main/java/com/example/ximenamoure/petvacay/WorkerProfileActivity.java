package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.example.ximenamoure.petvacay.Notifications.MyReceiver;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.example.ximenamoure.petvacay.UserRegistrationActivity.CLIENTID;

public class WorkerProfileActivity extends AppCompatActivity implements LocationListener {

    private int idWorker;
    private JSONObject workerData;
    private ProgressDialog progress;
    private ImageView imgWorker;
    private TextView mailWorker;
    private Button takeImageBtn;
    private Button disponibilityBtn;
    private Button saveBtn;
    private EditText shortDescription;
    private EditText information;
    private EditText boardingPrice;
    private CheckBox checkWalker;
    private EditText walkingPrice;
    private String workerAddress;
    private TextView myBookings;
    private RatingBar myRating;

    private Button imagesLink;

    private ArrayList<Integer> newDisponibilities;

    static final int TOMAR_FOTO = 100;
    Uri fileUri;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        int workerId=getIntent().getIntExtra("WORKER_ID",0);
        idWorker = workerId;

        imgWorker = (ImageView) findViewById(R.id.workerImage);
        mailWorker = (TextView) findViewById(R.id.workerEmail);
        takeImageBtn = (Button) findViewById(R.id.imagesBtn);
        disponibilityBtn = (Button) findViewById(R.id.disponibilityBtn);
        saveBtn = (Button) findViewById(R.id.saveDataBtn);
        shortDescription = (EditText) findViewById(R.id.shortDescription);
        information = (EditText) findViewById(R.id.Information);
        boardingPrice = (EditText) findViewById(R.id.boardingPrice);
        checkWalker = (CheckBox) findViewById(R.id.checkBoxWalkerUpdate);
        walkingPrice = (EditText) findViewById(R.id.walkingPrice);
        imagesLink = (Button) findViewById(R.id.imagesLink);
        myBookings = (TextView) findViewById(R.id.workerBookings);
        myRating = (RatingBar) findViewById(R.id.workerRatingBar);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.workerAddressFragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                workerAddress = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });

        getWorkerData(idWorker);

        imagesLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkerProfileActivity.this, WorkerImagesActivity.class);
                i.putExtra("WorkerID", idWorker);
                startActivity(i);
            }
        });

        checkWalker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    walkingPrice.setVisibility(View.VISIBLE);
                }
                else{
                    walkingPrice.setVisibility(View.GONE);
                }
            }
        });

        myBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WorkerProfileActivity.this, WorkerBookingsMenu.class);
                startActivity(i);
            }
        });

        TextView deleteBtn = (TextView) findViewById(R.id.deleteWorkerBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgressBar();
                deleteWorker();
            }
        });

        takeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getNewFile();
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, TOMAR_FOTO);
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            takeImageBtn.setEnabled(false);
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
        setNavigationDrawer();
        startNotificationListener();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        updateLocation();
    }

    private void updateLocation(){
        if (ContextCompat.checkSelfPermission(WorkerProfileActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(WorkerProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WorkerProfileActivity.this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 2);
        }
        else{
            locationManager.requestLocationUpdates(BackendConfig.GetInstance().LocationProvider, 10, 0, this);
            //it calls the onLocationChanged
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
                            case 2:
                                i = new Intent(WorkerProfileActivity.this, LoginActivity.class);
                                startActivity(i);
                                break;
                        };
                        return true;
                    }
                })
                .build();
    }

    private Uri getNewFile(){
        Uri uri = null;
        try{
            //it creates a folder misFotos in the directory of photos
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

    @Override //it is called when startActivityForResult returns a value
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try{
            if(requestCode == TOMAR_FOTO){
                if(resultCode == RESULT_OK){
                    //Cargo el ImageView con el fileUri
                    File imgFile = new File(fileUri.getPath());
                    SendImage(imgFile);
                }
            }
        }
        catch(Exception ex){
            Log.e("Error al volver de foto", ex.getMessage());
        }
    }

    private void startNotificationListener()
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.SECOND, 10);

        Intent i = new Intent(WorkerProfileActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(WorkerProfileActivity.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void deleteWorker(){
        String url = BackendConfig.GetInstance().BackendIP + "workers/" + idWorker;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progress.dismiss();
                        try{
                            Toast.makeText(WorkerProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            Thread.sleep(1000);
                            Intent i=new Intent(WorkerProfileActivity.this,LoginActivity.class);
                            startActivity(i);
                        }
                        catch (JSONException | InterruptedException ex) {}

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

                                progress.dismiss();
                                Toast.makeText(WorkerProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void SendImage(File imgFile) {

        String url = BackendConfig.GetInstance().BackendIP + "workers/" + idWorker + "/images";
        byte [] image = getImageByteArray(imgFile);
        JSONObject jsonToSend = makeJson(image);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(WorkerProfileActivity.this,"Imagen agregada exitosamente." , Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(WorkerProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
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
            json.put("Image", new JSONArray(image));
        }
        catch(JSONException ex) {}
        return json;
    }

    private void getWorkerData(int idWorker) {

        String url = BackendConfig.GetInstance().BackendIP + "workers/" + idWorker;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        sharedResponse(response);
                        setWorkerDataInView();
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

                                Toast.makeText(WorkerProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void sharedResponse(JSONObject response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("ResponseWorker", response.toString());
        editor.commit();
    }

    private void setWorkerDataInView(){
        try {
            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("ResponseWorker", "");
            workerData = new JSONObject(response);

            String email = workerData.get("email").toString();
            mailWorker.setText(email);

            String img = workerData.get("profileImage").toString();

            if(!img.equals("null")){
                byte[] imgBytes = Base64.decode(img, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                imgWorker.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgWorker.getWidth(),
                        imgWorker.getHeight(), false));
            }

            String shortdesc=workerData.get("shortDescription").toString();
            shortDescription.setText(shortdesc);

            String info=workerData.get("information").toString();
            information.setText(info);

            int boardPrice=workerData.getInt("boardingPrice");
            boardingPrice.setText(boardPrice + "");

            boolean isWalker=workerData.getBoolean("isWalker");
            if(isWalker){
                checkWalker.setChecked(true);
            }

            float workerRating = (float) workerData.getDouble("averageScore");
            myRating.setRating(workerRating);
 
            disponibilityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        JSONArray json = workerData.getJSONArray("disponibility");
                        String [] disponibilities = convertToArray(json);
                        final String[] days = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};
                        boolean [] disponibilitiesArray = getDisponibilitiesArray(disponibilities);

                        AlertDialog.Builder builder = new AlertDialog.Builder(WorkerProfileActivity.this);
                        builder.setTitle("Elija los dias disponibles");
                        builder.setMultiChoiceItems(days, disponibilitiesArray, new DialogInterface.OnMultiChoiceClickListener(){
                            public void onClick(DialogInterface dialogInterface, int item, boolean state) {
                            }
                        });
                        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newDisponibilities = new ArrayList<Integer>();
                                SparseBooleanArray days = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                                for(int i=0; i<days.size(); i++){
                                    int key = days.keyAt(i);
                                    if(days.get(key)){
                                        newDisponibilities.add(key);
                                    }
                                }
                            }
                        });

                        builder.show();
                    }
                    catch (JSONException ex) {}


                }
            });
        }
        catch(JSONException ex){

        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgressBar();
                SendInfo();
            }
        });

    }

    private void SendInfo(){
        String url = BackendConfig.GetInstance().BackendIP + "workers/" + idWorker;
        JSONObject jsonToSend = makeJsonToSend();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        Toast.makeText(WorkerProfileActivity.this, "Datos actualizados correctamente.", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(WorkerProfileActivity.this, WorkerProfileActivity.class);
                        i.putExtra("WORKER_ID", idWorker);
                        startActivity(i);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
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

                                Toast.makeText(WorkerProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private String [] convertToArray(JSONArray data){
        int length = data.length();
        String [] days = new String [length];
        try{
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    days[i] = data.getString(i);
                }
            }
        }
        catch(JSONException ex){}
        return days;
    }

    private boolean [] getDisponibilitiesArray(String [] disponibilites){
        final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        boolean [] ret = new boolean[days.length];
        for(int i=0; i<days.length;i++){
            if(containsDay(days[i], disponibilites)){
                ret[i] = true;
            }
        }
        return ret;
    }

    private boolean containsDay(String day, String [] disponibilities){
        for(String d : disponibilities){
            if(d.equals(day)){
                return true;
            }
        }
        return false;
    }

    private JSONObject makeJsonToSend() {
        JSONObject json = new JSONObject();
        String disponibilityAsString = "";
        boolean canSendDisponibilities = false;

        if(newDisponibilities != null){
            disponibilityAsString = Arrays.toString(newDisponibilities.toArray());
            canSendDisponibilities = true;
        }

        try{
            json.put("ShortDescription", shortDescription.getText().toString());
            json.put("Information", information.getText().toString());
            json.put("BoardingPrice", boardingPrice.getText().toString());

            if(checkWalker.isChecked()){
                json.put("WalkingPrice", walkingPrice.getText().toString());
            }

            json.put("Address",workerAddress);

            if(canSendDisponibilities){
                json.put("Disponibility", new JSONArray(disponibilityAsString));
            }
        }
        catch(JSONException ex) {}
        return json;
    }

    private void startProgressBar()
    {
        progress = new ProgressDialog(WorkerProfileActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }

    public void onClickHandler(View v) {
        Intent intent = new Intent(this, AddWorkerInfoToProfileActivity.class);
        intent.putExtra("WORKERID", idWorker);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takeImageBtn.setEnabled(true);
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
        String url = BackendConfig.GetInstance().BackendIP + "workers/" + idWorker + "/location";

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

                                progress.dismiss();
                                Toast.makeText(WorkerProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
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
