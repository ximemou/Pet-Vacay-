package com.example.ximenamoure.petvacay;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import android.content.Intent;

import static com.example.ximenamoure.petvacay.UserRegistrationActivity.CLIENTID;


public class PaymentActivity extends AppCompatActivity  {


    private EditText expirationDateText;

    private MonthYearPicker datePicker;

    private int amount;

    private TextView amountToPay;
    private EditText creditCardNumber;
    private EditText ccv;


    private int clientId;
    private int workerId;
    private int petId;
    private boolean isWalker;
    private String startDate;
    private String finishDate;

    private Button doPay;

    ProgressDialog progress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        clientId=getIntent().getIntExtra("clientId",0);
        workerId=getIntent().getIntExtra("workerId",0);
        petId=getIntent().getIntExtra("petId",0);
        isWalker=getIntent().getBooleanExtra("isWalker",false);
        startDate=getIntent().getStringExtra("startDate");
        finishDate=getIntent().getStringExtra("finishDate");
        amount=getIntent().getIntExtra("amount",0);

        expirationDateText=(EditText)findViewById(R.id.expirationDate);



        amountToPay=(TextView)findViewById(R.id.amountToPay);
        amountToPay.setText("Monto a pagar: $"+amount);
        creditCardNumber=(EditText)findViewById(R.id.creditCardNumber);
        ccv=(EditText)findViewById(R.id.ccv);

        datePicker = new MonthYearPicker(this);
        datePicker.build(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                expirationDateText.setText(datePicker.getSelectedMonthName() + " - " + datePicker.getSelectedYear());
            }
        }, null);
        
        expirationDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });

        doPay=(Button)findViewById(R.id.payButton);
        doPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPayment();
            }
        });
    }



    private void attemptPayment() {
        // Reset errors.
        expirationDateText.setError(null);
       creditCardNumber.setError(null);
        ccv.setError(null);

        // Store values at the time of the payment attempt.
        String creditCNumber = creditCardNumber.getText().toString();
        String creditCardCCV = ccv.getText().toString();
        String expirationDate=expirationDateText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid expirationDate, if the user entered one.

        if (TextUtils.isEmpty(expirationDate)) {
            expirationDateText.setError(getString(R.string.error_field_required));
            focusView = expirationDateText;
            cancel = true;
        }

        // Check for a valid credit card number.
        if (TextUtils.isEmpty(creditCNumber)) {
            creditCardNumber.setError(getString(R.string.error_field_required));
            focusView = creditCardNumber;
            cancel = true;
        } else if (creditCNumber.length()!=16 ) {
            creditCardNumber.setError("Debe contener 16 numeros");
            focusView = creditCardNumber;
            cancel = true;
        }

        if(TextUtils.isEmpty(creditCardCCV)){
            ccv.setError(getString(R.string.error_field_required));
            focusView = ccv;
            cancel = true;
        }
        else if(creditCardCCV.length()!=3){
            ccv.setError("Debe contener 3 numeros");
            focusView = ccv;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt payment and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            makeBooking();
        }
    }



    private void makeBooking(){


        String url = BackendConfig.GetInstance().BackendIP + "bookings";
        JSONObject dataToSend = makeJsonToSend();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        Toast.makeText(PaymentActivity.this, "Reserva realizada con exito", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent i=new Intent(PaymentActivity.this,SearchWorkersActivity.class);
                        i.putExtra("CLIENT_ID",clientId);

                        startActivity(i);
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
                                    msg = json.getString("exceptionMessage");
                                } catch (JSONException ex) {
                                }

                                Toast.makeText(PaymentActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);


    }



    private JSONObject makeJsonToSend(){
        JSONObject jsonToSend = new JSONObject();
        String convertedStartDate = convertDateFormat(startDate);
        String convertedFinishDate = convertDateFormat(finishDate);

        try{

            jsonToSend.put("clientId", clientId);
            jsonToSend.put("workerId", workerId);
            jsonToSend.put("petId", petId);
            jsonToSend.put("isWalker",isWalker);
            jsonToSend.put("startDate", convertedStartDate );
            jsonToSend.put("finishDate", convertedFinishDate);
            jsonToSend.put("creditCardNumber",creditCardNumber.getText().toString());
            jsonToSend.put("cCV",Integer.parseInt(ccv.getText().toString()));
            jsonToSend.put("creditCardExpirationMonth",datePicker.getSelectedMonth()+1);
            jsonToSend.put("creditCardExpirationYear",datePicker.getSelectedYear());
            jsonToSend.put("amount",amount);

        }
        catch(JSONException ex){}
        return jsonToSend;
    }

    private String convertDateFormat(String date){
        String convertedDate = date + "T07:22Z";
        return convertedDate;
    }

    private void startProgressBar()
    {
        progress = new ProgressDialog(PaymentActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }



}





