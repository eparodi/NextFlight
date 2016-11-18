package com.example.martin.nextflight;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.martin.nextflight.adapters.CommentsArrayAdapter;
import com.example.martin.nextflight.adapters.FlightArrayAdapter;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        TextView review_flight_number = (TextView)findViewById(R.id.review_flight_number_text_view);
        TextView review_airline_name = (TextView)findViewById(R.id.review_flight_airline_text_view);

        review_flight_number.setText("Vuelo " + "5620");
        review_airline_name.setText("Aerolìnea " + "Air Canada");

        String[] comments = new String[] {"HEY THERE!!", "HOLAAA"};
        CommentsArrayAdapter adapter = new CommentsArrayAdapter(this, comments);
        ListView listView = (ListView)this.findViewById(R.id.review_comments_list_view);
        if (listView != null) {
            listView.setAdapter(adapter);
        }
    }

}
