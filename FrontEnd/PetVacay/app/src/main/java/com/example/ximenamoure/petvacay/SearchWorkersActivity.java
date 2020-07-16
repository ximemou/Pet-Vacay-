package com.example.ximenamoure.petvacay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximenamoure.petvacay.Adapters.WorkerListAdapter;
import com.example.ximenamoure.petvacay.BackendConfig.BackendConfig;
import com.example.ximenamoure.petvacay.Models.Worker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SearchWorkersActivity extends AppCompatActivity {

    private int clientId;

    private EditText searchName;
    private CheckBox checkIsWalker;
    private Button searchBtn;
    private String addressQuery;
    private EditText addressEditText;

    private RecyclerView mRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private WorkerListAdapter mAdapter;
    private ArrayList<Worker> mWorkerList;

    private JSONArray workers;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_workers);

        clientId = getIntent().getIntExtra("CLIENT_ID",0);

        searchName = (EditText) findViewById(R.id.search_bar_workersName);
        checkIsWalker = (CheckBox) findViewById(R.id.checkBoxIsWalker);
        searchBtn = (Button) findViewById(R.id.searchWorkerBtn);

        setNavigationDrawer();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addressQuery = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });
        addressEditText = ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));

        mRecycleView = (RecyclerView) findViewById(R.id.workersContainer);
        mRecycleView.setNestedScrollingEnabled(false);

        mLinearLayoutManager = new LinearLayoutManager(SearchWorkersActivity.this);
        mRecycleView.setLayoutManager(mLinearLayoutManager);

        mWorkerList = new ArrayList<>();
        mAdapter = new WorkerListAdapter(SearchWorkersActivity.this, mWorkerList);
        mRecycleView.setAdapter(mAdapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgressBar();
                getWorkerData();
            }
        });
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
                                i = new Intent(SearchWorkersActivity.this, ClientProfileActivity.class);
                                i.putExtra("", clientId);
                                startActivity(i);
                                break;
//                            case 2:
//                                i = new Intent(SearchWorkersActivity.this, SearchWorkersActivity.class);
//                                i.putExtra("CLIENT_ID", clientId);
//                                startActivity(i);
//                                break;
                            case 3:
                                i = new Intent(SearchWorkersActivity.this, LookupWorkersMap.class);
                                startActivity(i);
                                break;
                            case 4:
                                i = new Intent(SearchWorkersActivity.this, ClientBookingsMenu.class);
                                startActivity(i);
                                break;
                            case 5:
                                i = new Intent(SearchWorkersActivity.this, LoginActivity.class);
                                startActivity(i);
                                break;
                        };
                        return true;
                    }
                })
                .build();
    }

    private void getWorkerData() {

        String url = buildUrl();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        progress.dismiss();
                        sharedResponse(response);
                        fillList();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        if (error.networkResponse.data != null) {
                            try {
                                String msg = new String(error.networkResponse.data, "UTF-8");
                                JSONObject json;
                                try {
                                    json = new JSONObject(msg);
                                    msg = json.getString("message");
                                } catch (JSONException ex) {
                                }

                                Toast.makeText(SearchWorkersActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException ex) {
                            }
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }

    private String buildUrl() {

        String url = BackendConfig.GetInstance().BackendIP + "workers/query";
        url += "?both=" + checkIsWalker.isChecked();
        if(searchName.getText().toString().length() > 0){
            url += "&name=" + searchName.getText().toString();
        }
        if(addressEditText.getText().length() > 0){
            url += "&address=" + addressQuery;
        }
        return url;
    }

    private void sharedResponse(JSONArray response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putString("ResponseFilteredWorkers", response.toString());
        editor.commit();
    }

    private void fillList(){
        try{
            mWorkerList.clear();
            mAdapter.notifyDataSetChanged();

            SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
            String response = m.getString("ResponseFilteredWorkers", "");
            workers = new JSONArray(response);

            ArrayList<Worker> workersList = convertToArrayOfWorkers(workers);
            mWorkerList.addAll(workersList);
            mAdapter.notifyDataSetChanged();
        }

        catch (JSONException ex) {}
    }

    private ArrayList<Worker> convertToArrayOfWorkers(JSONArray jsonArray)
    {
        ArrayList<Worker> workersArray = new ArrayList<Worker>();
        try{
            for(int i = 0; i<jsonArray.length(); i++)
            {
                Worker convertedWorker = new Worker(jsonArray.getJSONObject(i));
                workersArray.add(convertedWorker);
            }
        }
        catch (JSONException ex) {}
        return workersArray;
    }

    private void startProgressBar()
    {
        progress = new ProgressDialog(SearchWorkersActivity.this);
        progress.setTitle("Cargando");
        progress.setMessage("Esperando respuesta...");
        progress.setCancelable(false);
        progress.show();
    }
}
