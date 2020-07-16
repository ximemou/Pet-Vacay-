package com.example.ximenamoure.petvacay.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.ximenamoure.petvacay.Models.Booking;
import com.example.ximenamoure.petvacay.R;
import com.example.ximenamoure.petvacay.WorkerMakeInformsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class WorkerBookingsAdapter extends RecyclerView.Adapter<WorkerBookingsAdapter.BookingsHolder> {

    private ArrayList<Booking> mBookings;
    private Context mContext;
    private ProgressDialog progress;

    public WorkerBookingsAdapter(Context context, ArrayList<Booking> bookings){
        mBookings = bookings;
        mContext = context;
    }

    @Override
    public WorkerBookingsAdapter.BookingsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_worker_booking, parent, false);
        return new WorkerBookingsAdapter.BookingsHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(WorkerBookingsAdapter.BookingsHolder holder, int position) {
        final Booking itemBooking = mBookings.get(position);
        holder.bindBooking(itemBooking);

        holder.makeReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, WorkerMakeInformsActivity.class);
                i.putExtra("BOOKING_ID", itemBooking.GetBookingId());
                mContext.startActivity(i);
            }
        });

        holder.startWalkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress(mContext);
                startWalking(mContext, itemBooking.GetBookingId(), itemBooking.GetWorkerId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class BookingsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mBookingInfo;
        private TextView mBookingDays;
        public Button makeReportBtn;
        public Button startWalkingBtn;
        private Booking mBooking;

        public BookingsHolder(View v){
            super(v);
            mBookingInfo = (TextView) v.findViewById(R.id.workerBookingInfo);
            mBookingDays = (TextView) v.findViewById(R.id.workerBookingDays);
            makeReportBtn = (Button) v.findViewById(R.id.writeReportBtn);
            startWalkingBtn = (Button) v.findViewById(R.id.startWalkingBtn);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void bindBooking(Booking booking){
            mBooking = booking;
            mBookingInfo.setText("Reserva #" + mBooking.GetBookingId());
            mBookingDays.setText(mBooking.GetStartDay() + " - " + mBooking.GetFinishDay());

            if(mBooking.IsWalkingType() && !mBooking.WalkingStarted()){
                startWalkingBtn.setVisibility(View.VISIBLE);
            }
            else if(mBooking.IsWalkingType() && mBooking.WalkingStarted()){
                startWalkingBtn.setEnabled(false);
            }

        }
    }

    private void startProgress(Context context){
        progress = new ProgressDialog(context);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }

    private void startWalking(final Context context, int bookingId, final int workerId){
        String url = BackendConfig.GetInstance().BackendIP + "bookings/" + bookingId;
        JSONObject dataToSend = makeJsonToSend(context);

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        updateWorkerLocation(context, workerId);
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
                                Toast.makeText(context, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private JSONObject makeJsonToSend(Context context){
        JSONObject json = new JSONObject();

        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(context);
        String workerLatitude = m.getString("WorkerLocationLatitude", "");
        String workerLongitude = m.getString("WorkerLocationLongitude", "");

        try{
            json.put("Latitude", workerLatitude);
            json.put("Longitude", workerLongitude);
        }
        catch (JSONException ex) {}
        return json;
    }

    public void updateWorkerLocation(final Context context, final int workerId)
    {
        String url = BackendConfig.GetInstance().BackendIP + "workers/" + workerId + "/location";
        JSONObject dataToSend = makeJsonToSend(context);

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.PUT, url, dataToSend, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Paseo comenzado", Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(context, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }
}
