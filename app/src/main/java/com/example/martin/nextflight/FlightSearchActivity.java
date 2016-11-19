package com.example.martin.nextflight;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import status.Airline;

public class FlightSearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AutoCompleteTextView airline_name_input;
    private final String AIRLINE = "airlines";
    ArrayList<Airline> allAirlines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final EditText flight_number_input = (EditText) findViewById(R.id.flightNumber);
        airline_name_input = (AutoCompleteTextView) findViewById(R.id.airlineName);

        new HttpGetAirlines().execute();

        final Button button = (Button) findViewById(R.id.status_search_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean error = false;
                String flight_number = flight_number_input.getText().toString();
                String airline_name = airline_name_input.getText().toString();

                if ( flight_number.equals("") ){
                    flight_number_input.setError(getResources().getString(R.string.empty_number_validator));
                    error = true;
                }

                if ( airline_name.equals("") ){
                    airline_name_input.setError(getResources().getString(R.string.empty_airline_validator));
                    error = true;
                }else{
                    boolean found = false;
                    for ( Airline a : allAirlines ){
                        if ( a.getAirlineName().equals(airline_name)){
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        error = true;
                        airline_name_input.setError(getResources().getString(R.string.not_valid_airline));
                    }
                }
                if (!error){
                    // Go to the next activity.
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.flight_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_flight) {
            // Handle the camera action
        } else if (id == R.id.nav_offers) {

        } else if (id == R.id.nav_converter) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class HttpGetAirlines extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/misc.groovy?method=getairlines");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception exception) {
                exception.printStackTrace();
                return getResources().getString(R.string.error);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Airline>>() {
                }.getType();

                String jsonFragment = obj.getString(AIRLINE);
                ArrayList<Airline> airlineList = gson.fromJson(jsonFragment, listType);
                allAirlines = airlineList;

                ArrayAdapter<String> resultList = new ArrayAdapter<>(FlightSearchActivity.this,
                        android.R.layout.simple_dropdown_item_1line);

                for (Airline a : airlineList){
                    resultList.add(a.getAirlineName());
                }

                airline_name_input.setAdapter(resultList);
            }catch(Exception e){

                // TODO: Add a message.
            }

        }

        private String readStream(InputStream inputStream) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int i = inputStream.read();
                while (i != -1) {
                    outputStream.write(i);
                    i = inputStream.read();
                }
                return outputStream.toString();
            } catch (IOException e) {
                return "";
            }
        }
    }

    /*
    private class HttpGetStatus extends AsyncTask<Void, Void, String> {

        private String query;

        protected HttpGetStatus(String airline_id, String flight_number){
            super();
            try {
                query = "&airline_id=" + URLEncoder.encode(airline_id, "UTF-8") +
                        "&flight_number=" + URLEncoder.encode(flight_number, "UTF-8");
            }catch(Exception e){
                // TODO: Set messagges.
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/misc.groovy?method=getflightstatus" + query);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception exception) {
                exception.printStackTrace();
                return getResources().getString(R.string.error);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Airline>>() {
                }.getType();

                String jsonFragment = obj.getString(AIRLINE);
                ArrayList<Airline> airlineList = gson.fromJson(jsonFragment, listType);
                allAirlines = airlineList;

                ArrayAdapter<String> resultList = new ArrayAdapter<>(FlightSearchActivity.this,
                        android.R.layout.simple_dropdown_item_1line);

                airline_name_input = (AutoCompleteTextView)
                        findViewById(R.id.autoCompleteTextView);

                for (Airline a : airlineList){
                    resultList.add(a.getAirlineName());
                    Log.v("REQUEST",a.getAirlineId());
                }

                airline_name_input.setAdapter(resultList);
            }catch(Exception e){
                Log.v("REQUEST",e.toString());
                Log.v("REQUEST",result);
                // TODO: Add a message.
            }

        }

        private String readStream(InputStream inputStream) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int i = inputStream.read();
                while (i != -1) {
                    outputStream.write(i);
                    i = inputStream.read();
                }
                return outputStream.toString();
            } catch (IOException e) {
                return "";
            }
        }
    }
    */
}
