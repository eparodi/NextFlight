package com.example.martin.nextflight;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.martin.nextflight.elements.Airport;
import com.example.martin.nextflight.elements.City;
import com.example.martin.nextflight.elements.Deal;
import com.example.martin.nextflight.managers.OfferLocationListener;
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
    Location location;
    LocationListener locationListener;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_search);

        from_input = (AutoCompleteTextView) findViewById(R.id.from_option);
        resultList = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line);

        locationListener = new OfferLocationListener();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        if((location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))==null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        from_input.setAdapter(resultList);

        airportsTask = new HttpGetAirports();
        citiesTask = new HttpGetCities();

        airportsTask.execute();
        citiesTask.execute();

        Button searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) findViewById(R.id.location_box);
                if (checkBox.isChecked()){
                    ActivityCompat.requestPermissions(OffersSearchActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }else{
                    String text = from_input.getText().toString();
                    if (text.equals("")){
                        from_input.setError("Escriba el nombre del lugar de partida");
                        return;
                    }

                    Airport airport = null;
                    City city = null;

                    if ( airports == null ){
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.no_connection_error),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (Airport a : airports){
                        if (a.getDescription().equals(text)){
                            airport = a;
                        }
                    }
                    if (airport == null){
                        for (City c : cities){
                            if (c.getName().equals(text)){
                                city = c;
                            }
                        }
                    }else{
                        new HttpGetOffers(airport.getCity()).execute();
                    }
                    if (city == null){
                        from_input.setError("Seleccione una opción de la lista.");
                        return;
                    }

                    new HttpGetOffers(city).execute();
                }
            }
        });
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        if (checked) {
            from_input.setFocusable(false);
        } else {
            from_input.setFocusableInTouchMode(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new HttpGetClosestAirport().execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.error_no_ubication),
                            Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
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
        double longitude;
        double latitude;

        @Override
        protected String doInBackground(Void... params) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            HttpURLConnection urlConnection = null;

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
                ArrayList<Airport> airportList = gson.fromJson(jsonFragment, listType);

                Airport nearer = null;
                double minimum = Double.MAX_VALUE;

                for (Airport a : airportList) {
                    double dist = distance(a.getLongitude(),a.getLatitude(),longitude,latitude);
                    if (minimum > dist){
                        nearer = a;
                        minimum = dist;
                    }
                }
                if( nearer == null ){
                    Toast.makeText(getApplicationContext(),
                            R.string.error_no_close_airport,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // This prevents to get a City without name.
                City depCity = nearer.getCity();
                for (City c : cities){
                    if (c.getId().equals(depCity.getId())){
                        depCity = c;
                    }
                }
                new HttpGetOffers(depCity).execute();
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

    private class HttpGetOffers extends AsyncTask<Void, Void, String> {

        private final static String DEALS = "deals";

        private City city;

        HttpGetOffers(City city){
            this.city = city;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getflightdeals&from="+city.getId());
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
                Type listType = new TypeToken<ArrayList<Deal>>() {
                }.getType();

                String jsonFragment = obj.getString(DEALS);

                ArrayList<Deal> dealList = gson.fromJson(jsonFragment, listType);

                Intent intent = new Intent(getApplicationContext(), OffersResultActivity.class);

                PendingIntent pendingIntent =
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setContentIntent(pendingIntent);

                intent.putExtra("FlightList",dealList);
                intent.putExtra("DepartureCity",city);
                startActivity(intent);
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

}
