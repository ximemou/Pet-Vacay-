package com.example.ximenamoure.petvacay.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.ximenamoure.petvacay.ClientInformsActivity;
import com.example.ximenamoure.petvacay.EvaluateWorkerActivity;
import com.example.ximenamoure.petvacay.Models.Booking;
import com.example.ximenamoure.petvacay.Models.Pet;
import com.example.ximenamoure.petvacay.R;
import com.example.ximenamoure.petvacay.WalkingPetMap;
import com.example.ximenamoure.petvacay.WorkerProfileActivity;
import com.example.ximenamoure.petvacay.WorkerRegistrationActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ClientBookingsAdapter extends RecyclerView.Adapter<ClientBookingsAdapter.BookingsHolder> {

    private ArrayList<Booking> mBookings;
    private Context mContext;
    private ProgressDialog progress;

    public ClientBookingsAdapter(Context context, ArrayList<Booking> bookings){
        mBookings = bookings;
        mContext = context;
    }

    @Override
    public ClientBookingsAdapter.BookingsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.layout_client_booking, parent, false);
        return new ClientBookingsAdapter.BookingsHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ClientBookingsAdapter.BookingsHolder holder, int position) {
        final Booking itemBooking = mBookings.get(position);
        holder.bindBooking(itemBooking);

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress(mContext);
                cancelBooking(mContext, itemBooking.GetBookingId());
            }
        });

        holder.seeMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemBooking.WalkingStarted()){
                    Intent i = new Intent(mContext, WalkingPetMap.class);
                    i.putExtra("BOOKING_ID", itemBooking.GetBookingId());
                    mContext.startActivity(i);
                }
                else{
                    Toast.makeText(mContext, "El paseador aun no ha comenzado con este paseo.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.seeInformsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ClientInformsActivity.class);
                mContext.startActivity(i);
            }
        });

        holder.evaluateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, EvaluateWorkerActivity.class);
                i.putExtra("BOOKINGID", itemBooking.GetBookingId());
                mContext.startActivity(i);
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
        private TextView mBookingStartingDays;
        public Button cancelBtn;
        public Button seeMapBtn;
        public Button seeInformsBtn;
        public Button evaluateBtn;
        private Booking mBooking;

        private TextView mBookingFinishDate;

        public BookingsHolder(View v){
            super(v);
            mBookingInfo = (TextView) v.findViewById(R.id.bookingInfo);
            mBookingStartingDays = (TextView) v.findViewById(R.id.bookingStartingDay);
            mBookingFinishDate=(TextView) v.findViewById(R.id.bookingFinishDay);
            cancelBtn = (Button) v.findViewById(R.id.cancelBookingBtn);
            seeMapBtn = (Button) v.findViewById(R.id.seeMapWalkingBtn);
            seeInformsBtn = (Button) v.findViewById(R.id.seeInformsBtn);
            evaluateBtn = (Button) v.findViewById(R.id.evaluateBookingBtn);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void bindBooking(Booking booking){
            mBooking = booking;
            mBookingInfo.setText("Reserva #" + mBooking.GetBookingId());
            mBookingStartingDays.setText("Comienzo de la reserva: "+ mBooking.GetStartDay());
            mBookingFinishDate.setText("Fin de la reserva: "+ mBooking.GetFinishDay());

            if(mBooking.IsWalkingType() && mBooking.WalkingStarted()){
                seeMapBtn.setVisibility(View.VISIBLE);
            }

        }
    }

    private void cancelBooking(final Context context, int bookingId){
        String url = BackendConfig.GetInstance().BackendIP + "bookings/" + bookingId;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        Toast.makeText(context, "Reserva cancelada", Toast.LENGTH_SHORT).show();
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

    private void startProgress(Context context){
        progress = new ProgressDialog(context);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }
}
