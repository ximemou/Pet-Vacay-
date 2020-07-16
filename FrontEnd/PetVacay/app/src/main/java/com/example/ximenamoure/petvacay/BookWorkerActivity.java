package com.example.ximenamoure.petvacay;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.Adapters.PetListAdapter;
import com.example.ximenamoure.petvacay.Adapters.PetSelectorAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.Pet;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

public class BookWorkerActivity extends AppCompatActivity{

    private int workerId;
    private String workerPhone;
    private int clientId;

    private int duration;


    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private PetSelectorAdapter mAdapter;
    private ArrayList<Pet> mPetsList;
    JSONObject clientData;

    private TextView startDateText;
    private TextView finishDateText;
    private CheckBox bookingType;
    private Button btnCallWorker;
    private Button bookBtn;

    private int walkingPrice;
    private int boardingPrice;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_worker);

        workerId = getIntent().getIntExtra("SelectedWorker", 0);
        workerPhone = getIntent().getStringExtra("SelectedWorkerPhone");
        walkingPrice=getIntent().getIntExtra("WalkingPrice",0);
        boardingPrice=getIntent().getIntExtra("BoardingPrice",0);

        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        clientId = Integer.parseInt(m.getString("LoggedClient", ""));

        startDateText = (TextView) findViewById(R.id.startDateTxt);
        finishDateText = (TextView) findViewById(R.id.finishDateTxt);
        bookingType = (CheckBox) findViewById(R.id.checkboxBookingType);
        btnCallWorker = (Button) findViewById(R.id.btnCallSelectedWorker);
        bookBtn = (Button) findViewById(R.id.btnMakeBooking);

        mRecycleView = (RecyclerView) findViewById(R.id.petsContainerToBook);

        mRecycleView.setNestedScrollingEnabled(false);
        mLinearLayoutManager = new LinearLayoutManager(BookWorkerActivity.this);
        mRecycleView.setLayoutManager(mLinearLayoutManager);

        mPetsList = new ArrayList<>();
        mAdapter = new PetSelectorAdapter(BookWorkerActivity.this, mPetsList,clientId);
        mRecycleView.setAdapter(mAdapter);

        getClientPets();
        deletePetSharedPreference();

        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment();
            }
        });

        btnCallWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( ContextCompat.checkSelfPermission(BookWorkerActivity.this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions(BookWorkerActivity.this, new String[] {  Manifest.permission.CALL_PHONE }, 0);
                }
                else{
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+workerPhone));
                    startActivity(i);
                }
            }
        });

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBooking();
            }
        });


        startDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                startDateText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        finishDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                finishDateText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void deletePetSharedPreference(){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putInt("SelectedPet", 0);
        editor.commit();
    }

    private void showFragment(){

        SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd) {
                        int fixedMonthStart = monthStart + 1;
                        int fixedMonthEnd = monthEnd + 1;

                        java.util.Calendar[] days=view.getHighlightedDays();

                        duration= days.length-1;

                        startDateText.setText(yearStart + "-" + fixedMonthStart + "-" + dayStart);
                        finishDateText.setVisibility(View.VISIBLE);
                        finishDateText.setText(yearEnd + "-" + fixedMonthEnd + "-" + dayEnd);
                    }
                });
        smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
    }

    private void startProgressBar()
    {
        progress = new ProgressDialog(BookWorkerActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }

    private void getClientPets(){
        startProgressBar();
        String url = BackendConfig.GetInstance().BackendIP + "clients/" + clientId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        sharedResponse(response);
                        fillPets();


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

                                Toast.makeText(BookWorkerActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        editor.putString("ResponseClientData", response.toString());
        editor.commit();
    }

    private void fillPets()
    {
        try{
            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("ResponseClientData", "");
            clientData = new JSONObject(response);

            JSONArray jsonArray = clientData.getJSONArray("personalPets");
            ArrayList<Pet> pets = convertToArrayOfPets(jsonArray);
            mPetsList.addAll(pets);
            mAdapter.notifyItemInserted(mPetsList.size());
        }
        catch (JSONException ex) {}

    }

    private ArrayList<Pet> convertToArrayOfPets(JSONArray jsonArray)
    {
        ArrayList<Pet> pets = new ArrayList<Pet>();
        try{
            for(int i = 0; i<jsonArray.length(); i++)
            {
                Pet convertedPet = new Pet(jsonArray.getJSONObject(i));
                pets.add(convertedPet);
            }
        }
        catch (JSONException ex) {}

        return pets;
    }

    private void makeBooking(){

        attemptBooking();
    }


    private void attemptBooking(){
        // Reset errors.
        startDateText.setError(null);
        finishDateText.setError(null);


        String startDate = startDateText.getText().toString();
        String finishDate = finishDateText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(startDate)) {
            startDateText.setError(getString(R.string.error_field_required));
            focusView = startDateText;
            cancel = true;
        }

        if (TextUtils.isEmpty(finishDate)) {
            finishDateText.setError(getString(R.string.error_field_required));
            focusView = finishDateText;
            cancel = true;
        }

        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        int petId = m.getInt("SelectedPet", 0);
        if(petId==0){
            Toast.makeText(this, "Debe seleccionar una mascota", Toast.LENGTH_SHORT).show();
            cancel=true;
        }

        if (cancel) {
            // There was an error
            focusView.requestFocus();
        } else {

            callMakePayment();
        }
    }

    private void callMakePayment(){
        int amount=0;
        Intent intent=new Intent(BookWorkerActivity.this,PaymentActivity.class);
        if(bookingType.isChecked()){
            amount=walkingPrice;

        }
        else{
            amount=calculatePrice(duration,boardingPrice);
        }

        intent.putExtra("clientId", clientId);
        intent.putExtra("workerId", workerId);
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        int petId = m.getInt("SelectedPet", 0);
        intent.putExtra("petId", petId);
        intent.putExtra("isWalker",bookingType.isChecked());
        intent.putExtra("startDate", startDateText.getText().toString());
        intent.putExtra("finishDate", finishDateText.getText().toString());
        intent.putExtra("amount",amount);
        startActivity(intent);

    }

    private int calculatePrice(int duration,int price){
        return (duration*price);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnCallWorker.setEnabled(true);
            }
        }
    }
}
