package com.example.ximenamoure.petvacay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.Adapters.ClientBookingsAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.Booking;
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

public class ClientBookingsMenu extends AppCompatActivity {

    private int clientId;

    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private ClientBookingsAdapter mAdapter;
    private ArrayList<Booking> mBookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bookings_menu);

        setNavigationDrawer();

        mRecycleView = (RecyclerView) findViewById(R.id.clientBookingsContainer);
        mRecycleView.setNestedScrollingEnabled(false);

        mLinearLayoutManager = new LinearLayoutManager(ClientBookingsMenu.this);
        mRecycleView.setLayoutManager(mLinearLayoutManager);

        mBookingList = new ArrayList<>();
        mAdapter = new ClientBookingsAdapter(ClientBookingsMenu.this, mBookingList);
        mRecycleView.setAdapter(mAdapter);

        clientId = getLoggedClient();
        showBookings();
    }

    private void setNavigationDrawer(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Mi perfil");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Buscar trabajadores");
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(3).withName("Mapa de trabajadores");
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(4).withName("Mis reservas");
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("LogOut");

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3,
                        new DividerDrawerItem(),
                        item4,
                        new DividerDrawerItem(),
                        item5,
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        Intent i;
                        switch((int) drawerItem.getIdentifier()){
                            case 1:
                                i = new Intent(ClientBookingsMenu.this, ClientProfileActivity.class);
                                i.putExtra("", clientId);
                                startActivity(i);
                                break;
                            case 2:
                                i = new Intent(ClientBookingsMenu.this, SearchWorkersActivity.class);
                                i.putExtra("CLIENT_ID", clientId);
                                startActivity(i);
                                break;
                            case 3:
                                i = new Intent(ClientBookingsMenu.this, LookupWorkersMap.class);
                                startActivity(i);
                                break;
                            case 5:
                                i = new Intent(ClientBookingsMenu.this, LoginActivity.class);
                                startActivity(i);
                                break;
                        };
                        return true;
                    }
                })
                .build();
    }

    private int getLoggedClient(){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(m.getString("LoggedClient", ""));
    }

    private void showBookings(){
        String url = BackendConfig.GetInstance().BackendIP + "clients/"+ clientId + "/bookings";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        fillList(response);
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

                                Toast.makeText(ClientBookingsMenu.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private void fillList(JSONArray json){

        if(json.length()>0) {
            ArrayList<Booking> bookings = convertToArrayOfBookings(json);
            mBookingList.addAll(bookings);
            mAdapter.notifyItemInserted(mBookingList.size());
        }else{
            Toast.makeText(ClientBookingsMenu.this, "No posee reservas", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Booking> convertToArrayOfBookings(JSONArray jsonArray){
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Booking convertedBooking = new Booking(jsonArray.getJSONObject(i));
                bookings.add(convertedBooking);
            }
        } catch (JSONException ex) {
        }
        return bookings;
    }
}
