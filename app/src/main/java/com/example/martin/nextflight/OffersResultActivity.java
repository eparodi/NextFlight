package com.example.martin.nextflight;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.martin.nextflight.elements.City;
import com.example.martin.nextflight.elements.Deal;

import java.util.ArrayList;
import java.util.List;

public class OffersResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_result);
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        ArrayList<Deal> dealList = (ArrayList<Deal>) bundle.getSerializable("FlightList");
        City departureCity = (City) bundle.getSerializable("DepartureCity");

        ArrayAdapter<Deal> showList = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,dealList);

        ListView dealShowList = (ListView) findViewById(R.id.offer_list);
        dealShowList.setAdapter(showList);

        TextView departureCityText = (TextView) findViewById(R.id.departure_city);
        departureCityText.setText(getString(R.string.from) + departureCity.getName());
    }
}
