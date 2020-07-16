package com.example.ximenamoure.petvacay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class WorkerMakeInformsActivity extends AppCompatActivity {

    private int bookingId;

    private Spinner informDaySpinner;
    private EditText writeInformView;
    private Button makeInformBtn;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_make_informs);

        bookingId = getIntent().getIntExtra("BOOKING_ID", 0);

        informDaySpinner = (Spinner) findViewById(R.id.spinnerDays);
        writeInformView = (EditText) findViewById(R.id.informText);
        makeInformBtn = (Button) findViewById(R.id.makeInformBtn);

        setNavigationDrawer();
        getWorkerAvailableDaysForInform(bookingId);

        makeInformBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                registerInform();
            }
        });
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
                                i = new Intent(WorkerMakeInformsActivity.this, WorkerProfileActivity.class);
                                i.putExtra("WORKER_ID", getLoggedWorker());
                                startActivity(i);
                                break;
                            case 2:
                                i = new Intent(WorkerMakeInformsActivity.this, LoginActivity.class);
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

    private void getWorkerAvailableDaysForInform(int bookingId) {
        String url = BackendConfig.GetInstance().BackendIP + "bookings/"+ bookingId + "/informDays";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        verifyCurrentDay(response);
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

                                Toast.makeText(WorkerMakeInformsActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void verifyCurrentDay(JSONArray jsonArray){

        if(jsonArray.length() > 0){
            populateSpinner(jsonArray);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("No tiene ningun dia para evaluar de esta reserva el dia de hoy.");
            builder.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent i = new Intent(WorkerMakeInformsActivity.this, WorkerBookingsMenu.class);
                    startActivity(i);
                }
            });

            builder.show();
        }
    }

    private void populateSpinner(JSONArray jsonArray){
        ArrayList<String> dates = new ArrayList<>();
        try {

            for(int i=0; i < jsonArray.length(); i++){
                String date = jsonArray.getString(i);
                dates.add(date);
            }
        } catch (JSONException e) {}

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dates);
        informDaySpinner.setAdapter(adapter);

    }

    private JSONObject makeJsonToSend(){
        JSONObject json = new JSONObject();
        try{
            json.put("BookingId", bookingId);
            json.put("DateOfInform", informDaySpinner.getSelectedItem().toString());
            json.put("InformData", writeInformView.getText().toString());
        }
        catch(JSONException ex){}
        return json;
    }

    private void registerInform(){
        String url = BackendConfig.GetInstance().BackendIP + "informs";
        JSONObject dataToSend = makeJsonToSend();

        RequestQueue queue = Volley.newRequestQueue(WorkerMakeInformsActivity.this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        Toast.makeText(WorkerMakeInformsActivity.this, "Informe registrado", Toast.LENGTH_SHORT).show();
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
                                    msg = json.getString("exceptionMessage");
                                }
                                catch(JSONException ex) {}

                                progress.dismiss();
                                Toast.makeText(WorkerMakeInformsActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void showProgress()
    {
        progress = new ProgressDialog(WorkerMakeInformsActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }
}
