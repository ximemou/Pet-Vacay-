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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.Adapters.InformsAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.Inform;
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

public class ClientInformsActivity extends AppCompatActivity {

    private int clientId;

    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private InformsAdapter mAdapter;
    private ArrayList<Inform> mInformList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_informs);

        clientId = getLoggedClient();

        setNavigationDrawer();

        mRecycleView = (RecyclerView) findViewById(R.id.clientInformsContainer);
        mRecycleView.setNestedScrollingEnabled(false);

        mLinearLayoutManager = new LinearLayoutManager(ClientInformsActivity.this);
        mRecycleView.setLayoutManager(mLinearLayoutManager);

        mInformList = new ArrayList<>();
        mAdapter = new InformsAdapter(ClientInformsActivity.this, mInformList);
        mRecycleView.setAdapter(mAdapter);

        getClientInforms();
    }

    private int getLoggedClient(){
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(m.getString("LoggedClient", ""));
    }

    private void setNavigationDrawer(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        clientId = getLoggedClient();
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
                                i = new Intent(ClientInformsActivity.this, ClientProfileActivity.class);
                                i.putExtra("", clientId);
                                startActivity(i);
                                break;
                            case 2:
                                i = new Intent(ClientInformsActivity.this, SearchWorkersActivity.class);
                                i.putExtra("CLIENT_ID", clientId);
                                startActivity(i);
                                break;
                            case 3:
                                i = new Intent(ClientInformsActivity.this, LookupWorkersMap.class);
                                startActivity(i);
                                break;
                            case 5:
                                i = new Intent(ClientInformsActivity.this, LoginActivity.class);
                                startActivity(i);
                                break;
                        };
                        return true;
                    }
                })
                .build();
    }

    private void getClientInforms(){
        String url = BackendConfig.GetInstance().BackendIP + "clients/"+ clientId + "/informs";

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

                                Toast.makeText(ClientInformsActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        ArrayList<Inform> informs = convertToArrayOfInforms(json);
        mInformList.addAll(informs);
        mAdapter.notifyItemInserted(mInformList.size());
    }

    private ArrayList<Inform> convertToArrayOfInforms(JSONArray jsonArray){
        ArrayList<Inform> informs = new ArrayList<Inform>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Inform convertedInform = new Inform(jsonArray.getJSONObject(i));
                informs.add(convertedInform);
            }
        } catch (JSONException ex) {
        }
        return informs;
    }
}
