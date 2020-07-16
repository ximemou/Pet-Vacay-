package com.example.ximenamoure.petvacay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class InformDetailsActivity extends AppCompatActivity {

    private int informId;
    private String informData;
    private int clientId;

    private TextView titleText;
    private TextView dataText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform_details);

        informId = getIntent().getIntExtra("INFORM_ID", 0);
        informData = getIntent().getStringExtra("INFORM_DATA");

        titleText = (TextView) findViewById(R.id.textInformId);
        dataText = (TextView) findViewById(R.id.textInformDetails);

        setContent();
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
                                i = new Intent(InformDetailsActivity.this, ClientProfileActivity.class);
                                i.putExtra("", clientId);
                                startActivity(i);
                                break;
                            case 2:
                                i = new Intent(InformDetailsActivity.this, SearchWorkersActivity.class);
                                i.putExtra("CLIENT_ID", clientId);
                                startActivity(i);
                                break;
                            case 3:
                                i = new Intent(InformDetailsActivity.this, LookupWorkersMap.class);
                                startActivity(i);
                                break;
                            case 5:
                                i = new Intent(InformDetailsActivity.this, LoginActivity.class);
                                startActivity(i);
                                break;
                        };
                        return true;
                    }
                })
                .build();
    }

    private void setContent(){
        titleText.setText("Informe #" + informId);
        dataText.setText(informData);
    }
}
