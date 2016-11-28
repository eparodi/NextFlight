package com.example.martin.nextflight;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.martin.nextflight.adapters.FlightArrayAdapter;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.managers.ScreenUtility;

import java.util.ArrayList;

public class FollowedFlightsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_flights);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ScreenUtility screenUtility = new ScreenUtility(this);

        Bundle bundle = getIntent().getExtras();
        ArrayList<Flight> flights = (ArrayList<Flight>) bundle.getSerializable("CHANGED_FLIGHTS");
        ListView view = (ListView) findViewById(R.id.followed_flights_list_view2);
        FlightArrayAdapter flights_adapter = new FlightArrayAdapter(this, flights, screenUtility);
        view.setAdapter(flights_adapter);

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(0);
    }

}
