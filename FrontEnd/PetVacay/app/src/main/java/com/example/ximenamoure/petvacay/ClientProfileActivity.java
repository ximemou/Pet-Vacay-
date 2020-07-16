package com.example.ximenamoure.petvacay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.Adapters.PetListAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.Pet;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.example.ximenamoure.petvacay.UserRegistrationActivity.CLIENTID;
import android.view.View;

public class ClientProfileActivity extends AppCompatActivity {


    private JSONObject clientData;
    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private PetListAdapter mAdapter;
    private ArrayList<Pet> mPetsList;

    private int idClient;
    private TextView myBookings;
    private Drawer result;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        int clientId=getIntent().getIntExtra(CLIENTID,0);
        idClient = clientId;
        getClientData(clientId);

        setNavigationDrawer();

        mRecycleView = (RecyclerView) findViewById(R.id.petsContainer);

        mRecycleView.setNestedScrollingEnabled(false);
        mLinearLayoutManager = new LinearLayoutManager(ClientProfileActivity.this);
        mRecycleView.setLayoutManager(mLinearLayoutManager);

        mPetsList = new ArrayList<>();
        mAdapter = new PetListAdapter(ClientProfileActivity.this, mPetsList,clientId);
        mRecycleView.setAdapter(mAdapter);

        myBookings = (TextView) findViewById(R.id.clientBookings);
        myBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClientProfileActivity.this, ClientBookingsMenu.class);
                startActivity(i);
            }
        });

        TextView deleteBtn = (TextView) findViewById(R.id.deleteClientBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgressBar();
                deleteClient();
            }
        });
    }

    private void startProgressBar()
    {
        progress = new ProgressDialog(ClientProfileActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }

    private void deleteClient(){
        String url = BackendConfig.GetInstance().BackendIP + "clients/" + idClient;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progress.dismiss();
                        try{
                            Toast.makeText(ClientProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            Thread.sleep(1000);
                            Intent i=new Intent(ClientProfileActivity.this,LoginActivity.class);
                            startActivity(i);
                        }
                        catch (JSONException | InterruptedException ex) {}

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
                                Toast.makeText(ClientProfileActivity.this, msg , Toast.LENGTH_SHORT).show();
                            }
                            catch(UnsupportedEncodingException ex) {}
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
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
                            case 2:
                                i = new Intent(ClientProfileActivity.this, SearchWorkersActivity.class);
                                i.putExtra("CLIENT_ID", idClient);
                                startActivity(i);
                                break;
                            case 3:
                                i = new Intent(ClientProfileActivity.this, LookupWorkersMap.class);
                                startActivity(i);
                                break;
                            case 4:
                                i = new Intent(ClientProfileActivity.this, ClientBookingsMenu.class);
                                startActivity(i);
                                break;
                            case 5:
                                i = new Intent(ClientProfileActivity.this, LoginActivity.class);
                                startActivity(i);
                                break;
                        };
                        return true;
                    }
                })
                .build();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }


    private void sharedResponse(JSONObject response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("Response", response.toString());
        editor.commit();
    }

    private void setClientDataInView()  {

        try {
            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("Response", "");
            clientData = new JSONObject(response);

            String email = clientData.get("email").toString();
            TextView emailText = (TextView) findViewById(R.id.clientEmail);
            emailText.setText(email);

            ImageView profileImage = (ImageView) findViewById(R.id.clientImage);
            String img = clientData.get("profileImage").toString();

            if(!img.equals("null")){
                byte[] imgBytes = Base64.decode(img, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                profileImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, profileImage.getWidth(),
                        profileImage.getHeight(), false));
            }


        }
        catch(JSONException ex){

        }
    }

    private void fillPets()
    {
        try{
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


    private void getClientData(int id) {

        String url = BackendConfig.GetInstance().BackendIP + "clients/" + id;


        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        sharedResponse(response);
                        setClientDataInView();
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

                                Toast.makeText(ClientProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);

    }

    public void onClickHandler(View v) {
        Intent intent = new Intent(this, AddClientInfoToProfileActivity.class);
        intent.putExtra("CLIENT_ID", idClient);
        startActivity(intent);
    }

    public void addPet(View v){
        Intent intent = new Intent(this, AddPetActivity.class);
        intent.putExtra("CLIENT_ID", idClient);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ClientProfileActivity.this, SearchWorkersActivity.class);
        i.putExtra("CLIENT_ID", idClient);
        startActivity(i);
    }
}
