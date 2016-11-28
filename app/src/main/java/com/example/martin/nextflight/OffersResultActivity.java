package com.example.martin.nextflight;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.adapters.OffersArrayAdapter;
import com.example.martin.nextflight.elements.City;
import com.example.martin.nextflight.elements.Deal;
import com.example.martin.nextflight.elements.flickr.FlickrObject;
import com.example.martin.nextflight.elements.flickr.Photo;
import com.example.martin.nextflight.elements.oneWayFlight.OneWayFlight;
import com.example.martin.nextflight.holders.OffersViewHolder;
import com.example.martin.nextflight.managers.SettingsManager;
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
import java.net.URLEncoder;
import java.util.ArrayList;

public class OffersResultActivity extends AppCompatActivity {

    private ArrayList<OneWayFlight> allFlights;
    private City departureCity;
    private ArrayList<Deal> dealList;
    private OffersArrayAdapter showList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_result);
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        dealList = ((ArrayList<Deal>) bundle.getSerializable("FlightList"));
        departureCity = (City) bundle.getSerializable("DepartureCity");

        // Comentado para que no se carguen las fotos y se pueda usar la app. Al descomentarlo,
        // descomentar tambien el momento en que se cargan en la view las imagenes. (OffersArrayAdapter).

        String currency = SettingsManager.getCurrency();

        showList = new OffersArrayAdapter(this, dealList, currency);

        ListView dealShowList = (ListView) findViewById(R.id.offer_list);
        dealShowList.setAdapter(showList);

        //for (Deal deal : dealList) {
        //    new HttpGetPhotos(deal).execute();
        //}

        dealShowList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OffersResultActivity.this, FlightStatusActivity.class);
                boolean found;
                found = fillIntent(intent, dealList.get(position), departureCity);
                PendingIntent pendingIntent =
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setContentIntent(pendingIntent);

                if(found)
                    startActivity(intent);
            }
        });

        TextView departureCityText = (TextView) findViewById(R.id.departure_city);
        departureCityText.setText(getString(R.string.from) + departureCity.getName());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_offer_map:
                Intent intent = new Intent(getApplicationContext(), AllOffersMapActivity.class);

                PendingIntent pendingIntent =
                        android.support.v4.app.TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(getApplicationContext());
                builder.setContentIntent(pendingIntent);

                intent.putExtra("FlightList",dealList);
                intent.putExtra("DepartureCity",departureCity);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean fillIntent(Intent intent, Deal deal, City dep_city) {

        String from = dep_city.getId();
        String to = deal.getCity().getId();
        String date = "2016-12-2";
        Double price = deal.getPrice();
        final Integer week_days = 7;

        Toast.makeText(getApplicationContext(), price.toString(), Toast.LENGTH_SHORT).show();

        for (Integer i = 0; i < week_days; i++) {
            String dep_date = date + i.toString();
            new HttpGetFlight(from, to, dep_date).execute();
            if (allFlights != null && !allFlights.isEmpty()) {
                for (OneWayFlight flight : allFlights) {
                    if (flight.getPrice().getTotal().getTotal() == price) {
                        intent.putExtra("FlightNumber", flight.getOutbound_routes().get(0).getSegments().get(0).getNumber().toString());
                        intent.putExtra("AirlineId", flight.getOutbound_routes().get(0).getSegments().get(0).getAirline().getAirlineId());
                        return true;
                    }
                }
            }
        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_offers_result_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class HttpGetFlight extends AsyncTask<Void, Void, String> {

        private String from_id;
        private String to_id;
        private String dep_date;

        public HttpGetFlight(String from_id, String to_id, String dep_date) {
            this.from_id = from_id;
            this.to_id = to_id;
            this.dep_date = dep_date;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            String from = "&from=" + from_id;
            String to = "&to=" + to_id;
            String date = "&dep_date=" + dep_date;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/booking.groovy?method=getonewayflights" + from + to + date + "&adults=1&children=0&infants=0&sort_key=total");
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
                Type listType = new TypeToken<ArrayList<OneWayFlight>>() {
                }.getType();

                String jsonFragment = obj.getString("flights");
                ArrayList<OneWayFlight> flight_list = gson.fromJson(jsonFragment, listType);
                allFlights = flight_list;

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