package com.example.martin.nextflight;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.martin.nextflight.elements.Airline;
import com.example.martin.nextflight.elements.Airport;
import com.example.martin.nextflight.elements.City;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OffersSearchActivity extends AppCompatActivity {

    AutoCompleteTextView from_input;
    ArrayList<City> cities;
    ArrayList<Airport> airports;
    ArrayAdapter<String> resultList;
    HttpGetAirports airportsTask;
    HttpGetCities citiesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_search);

        from_input = (AutoCompleteTextView) findViewById(R.id.from_option);
        resultList = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line);

        from_input.setAdapter(resultList);

        airportsTask = new HttpGetAirports();
        citiesTask = new HttpGetCities();

        airportsTask.execute();
        citiesTask.execute();


    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        if (checked) {
            from_input.setFocusable(false);
        } else {
            from_input.setFocusableInTouchMode(true);
        }
    }

    private class HttpGetCities extends AsyncTask<Void, Void, String> {

        private final static String CITIES = "cities";

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getcities");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_connection_error),
                        Toast.LENGTH_SHORT).show();
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
                Type listType = new TypeToken<ArrayList<City>>() {
                }.getType();

                String jsonFragment = obj.getString(CITIES);
                ArrayList<City> airlineList = gson.fromJson(jsonFragment, listType);
                cities = airlineList;

                for (City a : airlineList) {
                    resultList.add(a.getName());
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT).show();
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

    private class HttpGetAirports extends AsyncTask<Void, Void, String> {

        private final static String AIRPORTS = "airports";

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getairports");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_connection_error),
                        Toast.LENGTH_SHORT).show();
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
                Type listType = new TypeToken<ArrayList<Airport>>() {
                }.getType();

                String jsonFragment = obj.getString(AIRPORTS);
                ArrayList<Airport> airlineList = gson.fromJson(jsonFragment, listType);
                airports = airlineList;

                for (Airport a : airlineList) {
                    resultList.add(a.getDescription());
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT).show();
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

    private class HttpGetClosestAirport extends AsyncTask<Void, Void, String> {

        private final static String AIRPORTS = "airports";
        private final static int RADIUS = 40;
        private double latitude;
        private double longitude;

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);

            Location l = locationManager.getLastKnownLocation(Context.LOCATION_SERVICE);
            latitude = l.getLatitude();
            longitude = l.getLongitude();

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getairportsbyposition&latitude="+latitude+"&longitude="+longitude+"&radius="+RADIUS);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_connection_error),
                        Toast.LENGTH_SHORT).show();
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
                Type listType = new TypeToken<ArrayList<Airport>>() {
                }.getType();

                String jsonFragment = obj.getString(AIRPORTS);
                ArrayList<Airport> airlineList = gson.fromJson(jsonFragment, listType);
                airports = airlineList;

                Airport nearer;
                double minimum = Double.MAX_VALUE;

                for (Airport a : airlineList) {
                    double dist = distance(a.getLongitude(),a.getLatitude(),longitude,latitude);
                    if (minimum > dist){
                        nearer = a;
                        minimum = dist;
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT).show();
            }

        }

        private double distance(double x1, double y1, double x2, double y2){
            return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
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

}
