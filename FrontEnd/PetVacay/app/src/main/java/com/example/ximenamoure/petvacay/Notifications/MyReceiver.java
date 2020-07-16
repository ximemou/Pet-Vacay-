package com.example.ximenamoure.petvacay.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
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
import com.example.ximenamoure.petvacay.LoginActivity;
import com.example.ximenamoure.petvacay.R;
import com.example.ximenamoure.petvacay.WorkerBookingsMenu;
import com.example.ximenamoure.petvacay.WorkerProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ismap on 18/06/2017.
 */

public class MyReceiver extends BroadcastReceiver {

    private int MID=0;

    private Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        checkHasToInform();
    }

    private void checkHasToInform() {

        int workerId = getLoggedWorker();

        if(workerId != 0){
            checkBookingDaysToInform(workerId);
        }
    }

    private int getLoggedWorker(){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(ctx);
        return Integer.parseInt(m.getString("LoggedWorker", ""));
    }

    private void checkBookingDaysToInform(int workerId){
        String url = BackendConfig.GetInstance().BackendIP + "workers/"+ workerId + "/hasBookingsToday";

        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        showNotification();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        if (error.networkResponse.data != null) {
//                            try {
//                                String msg = new String(error.networkResponse.data, "UTF-8");
//                                JSONObject json;
//                                try {
//                                    json = new JSONObject(msg);
//                                    msg = json.getString("message");
//                                } catch (JSONException ex) {
//                                }
//
//                                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
//                            } catch (UnsupportedEncodingException ex) {
//                            }
//                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void showNotification(){

        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(ctx, WorkerProfileActivity.class);
        notificationIntent.putExtra("WORKER_ID", getLoggedWorker());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                ctx).setSmallIcon(R.drawable.paymenticon)
                .setContentTitle("PetVacay")
                .setContentText("Tiene una reserva para realizar informe.").setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(MID, mNotifyBuilder.build());
        MID++;
    }
}
