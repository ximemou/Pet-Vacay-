package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.constraint.solver.LinearSystem;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.Adapters.DayOfWeekListAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.DayOfWeek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkerRegistrationActivity extends AppCompatActivity implements LocationListener{

    private EditText txtMail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private ListView listDisponibility;
    private CheckBox checkBoxWalker;
    private ArrayList<DayOfWeek> daysDisponible;
    private DayOfWeekListAdapter adapter;
    private Button btnRegisterUser;

    private Location location;

    private CardView cardViewDisponibility;

    public static final String WORKERID="WORKER_ID";

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_registration);

        txtMail = (EditText) findViewById(R.id.txtMail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        checkBoxWalker = (CheckBox) findViewById(R.id.checkBoxWalker);

        listDisponibility = (ListView) findViewById(R.id.listDays);

        listDisponibility.setNestedScrollingEnabled(false);

        daysDisponible = new ArrayList<>();
        cardViewDisponibility=(CardView)findViewById(R.id.disponibilityCardView);

        fillDaysList();

        LocationManager l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(WorkerRegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(WorkerRegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WorkerRegistrationActivity.this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 2);
        }
        else{
            l.requestLocationUpdates(BackendConfig.GetInstance().LocationProvider, 10, 0, this);

        }

        adapter = new DayOfWeekListAdapter(this,daysDisponible);
        listDisponibility.setAdapter(adapter);

        listDisponibility.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DayOfWeek day = (DayOfWeek) parent.getItemAtPosition(position);
                day.SetChecked(!day.IsChecked());
                adapter.notifyDataSetChanged();
            }
        });


        checkBoxWalker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if ( isChecked )
                {
                    cardViewDisponibility.setVisibility(View.VISIBLE);

                }
                if(!isChecked){
                    cardViewDisponibility.setVisibility(View.GONE);
                }
            }
        });


        btnRegisterUser = (Button) findViewById(R.id.btnRegisterUser);
        btnRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempRegister();
            }
        });
    }

    public void showArray(View v) {

        String jsonArray = convertDaysOfWeekToInt();
        Toast.makeText(this, jsonArray , Toast.LENGTH_SHORT).show();

    }

    private String convertDaysOfWeekToInt() {
        ArrayList<Integer> ret = new ArrayList<>();

        for(int i=0; i<daysDisponible.size();i++){
            if(daysDisponible.get(i).IsChecked()){
                ret.add(i);
            }
        }
        return Arrays.toString(ret.toArray());
    }

    private void fillDaysList() {
        daysDisponible.add(new DayOfWeek("Domingo", false));
        daysDisponible.add(new DayOfWeek("Lunes", false));
        daysDisponible.add(new DayOfWeek("Martes", false));
        daysDisponible.add(new DayOfWeek("Miercoles", false));
        daysDisponible.add(new DayOfWeek("Jueves", false));
        daysDisponible.add(new DayOfWeek("Viernes", false));
        daysDisponible.add(new DayOfWeek("Sabado", false));
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }



    private boolean checkConfirmPassword(String password,String confirmPassword) {
        if(!(password).equals(confirmPassword)) {
           return false;

        }
        else{
            return true;
        }
    }

    private void attempRegister(){

            // Reset errors.
            txtPassword.setError(null);
            txtConfirmPassword.setError(null);
            txtMail.setError(null);

            // Store values at the time of the login attempt.
            String email =  txtMail.getText().toString();
            String password = txtPassword.getText().toString();
        String confirmPassword=txtConfirmPassword.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.

            if (TextUtils.isEmpty(password)) {
                txtPassword.setError(getString(R.string.error_field_required));
                focusView = txtPassword;
                cancel = true;
            }
            else if(!isPasswordValid(password)){
                txtPassword.setError("La contraseña debe tener al menos 8 caracteres");
                focusView = txtPassword;
                cancel = true;
            }
            else if (!checkConfirmPassword(password,confirmPassword)) {
                txtConfirmPassword.setError("Las contraseñas no coinciden");
                focusView = txtConfirmPassword;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                txtMail.setError(getString(R.string.error_field_required));
                focusView = txtMail;
                cancel = true;
            } else if (!isEmailValid(email)) {
                txtMail.setError(getString(R.string.error_invalid_email));
                focusView = txtMail;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                startProgressBar();
                registerWorker();
            }
    }


    public void registerWorker()
    {
        String url = BackendConfig.GetInstance().BackendIP + "workers";
        JSONObject dataToSend = makeJsonToSend();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            progress.dismiss();
                            Toast.makeText(WorkerRegistrationActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            saveLoggedUser();
                            Intent i = new Intent(WorkerRegistrationActivity.this, WorkerProfileActivity.class);
                            i.putExtra(WORKERID, Integer.parseInt(response.get("message").toString()));
                            startActivity(i);

                        }
                        catch(JSONException ex){
                            Toast.makeText(WorkerRegistrationActivity.this, "No se ha podido registrar", Toast.LENGTH_SHORT).show();
                        }

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
                                Toast.makeText(WorkerRegistrationActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void saveLoggedUser() {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("LoggedWorker", WORKERID);
        editor.commit();
    }

    private JSONObject makeJsonToSend()
    {
        String jsonArray = convertDaysOfWeekToInt();

        JSONObject json = new JSONObject();
        try{
            json.put("Email", txtMail.getText().toString());
            json.put("Password", txtPassword.getText().toString());
            json.put("RepeatedPassword", txtConfirmPassword.getText().toString());
            json.put("IsWalker", checkBoxWalker.isChecked());

            json.put("Disponibility", new JSONArray(jsonArray));
            json.put("Latitude", Double.toString(location.getLatitude()));
            json.put("Longitude", Double.toString(location.getLongitude()));
        }
        catch(JSONException ex) {}
        return json;
    }

    private void startProgressBar()
    {
        progress = new ProgressDialog(WorkerRegistrationActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(WorkerRegistrationActivity.this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {

        this.location = location;
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
