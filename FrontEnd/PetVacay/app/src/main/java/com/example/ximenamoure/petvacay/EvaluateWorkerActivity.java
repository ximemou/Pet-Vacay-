package com.example.ximenamoure.petvacay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

import java.io.UnsupportedEncodingException;

public class EvaluateWorkerActivity extends AppCompatActivity {

    private int bookingId;

    private RatingBar ratingBar;
    private EditText commentsText;
    private Button makeReviewBtn;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_worker);

        bookingId = getIntent().getIntExtra("BOOKINGID", 0);

        ratingBar = (RatingBar) findViewById(R.id.scoreRatingBar);
        commentsText = (EditText) findViewById(R.id.commentsText);
        makeReviewBtn = (Button) findViewById(R.id.makeReviewBtn);

        makeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                makeReview();
            }
        });
    }

    private void startProgress()
    {
        progress = new ProgressDialog(EvaluateWorkerActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }

    private void makeReview(){
        String url = BackendConfig.GetInstance().BackendIP + "reviews";
        JSONObject dataToSend = makeJsonToSend();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        Toast.makeText(EvaluateWorkerActivity.this, "Trabajador calificado correctamente", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EvaluateWorkerActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private JSONObject makeJsonToSend(){
        JSONObject json = new JSONObject();
        int calification = (int) ratingBar.getRating();
        String comment = commentsText.getText().toString();

        try{
            json.put("Score", calification);
            json.put("Comment", comment);
            json.put("BookingId", bookingId);
        }
        catch (JSONException ex) {}
        return json;
    }
}
