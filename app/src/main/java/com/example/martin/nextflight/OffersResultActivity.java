package com.example.martin.nextflight;

import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.adapters.OffersArrayAdapter;
import com.example.martin.nextflight.elements.Airline;
import com.example.martin.nextflight.elements.City;
import com.example.martin.nextflight.elements.Deal;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.elements.flickr.FlickrObject;
import com.example.martin.nextflight.elements.flickr.Photo;
import com.example.martin.nextflight.elements.oneWayFlight.OneWayFlight;
import com.example.martin.nextflight.managers.SettingsManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class OffersResultActivity extends AppCompatActivity {

    private ArrayList<OneWayFlight> allFlights;
    private City departureCity;
    private ArrayList<Deal> dealList;

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

        /*
        for (Deal deal : dealList) {
            new HttpGetPhotos(deal).execute();
        }
        */

        String currency = SettingsManager.getCurrency();

        OffersArrayAdapter showList = new OffersArrayAdapter(this, dealList, currency);

        ListView dealShowList = (ListView) findViewById(R.id.offer_list);
        dealShowList.setAdapter(showList);

        dealShowList.setAdapter(showList);
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

    private class HttpGetPhotos extends AsyncTask<Void, Void, String> {

        private Deal deal;

        public HttpGetPhotos(Deal deal) {
            this.deal = deal;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            String method = "&method=flickr.photos.search";
            String api_key = "&api_key=386aa6fb081b08ba87dd7efaf75f04f0";
            String per_page = "&per_page=1";
            String text = "&text=" + "";
            String format = "&format=json";

            try {
                text += URLEncoder.encode(deal.getCity().getName(), "UTF-8");
            } catch(Exception exception) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_connection_error),
                        Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
                return getResources().getString(R.string.error);
            }


            try {
                URL url = new URL("https://api.flickr.com/services/rest/?" + method + api_key + per_page + text + format);
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
                Type listType = new TypeToken<FlickrObject>() {
                }.getType();

                String jsonFragment = obj.getString("photos");
                FlickrObject flickrObject = gson.fromJson(jsonFragment, listType);

                // Esto es solo para probar si funciona el LoadImage, usando la imagen del ejemplo.
                new LoadImage(deal).execute("http://itba.edu.ar/sites/default/themes/itba/assets/images/back.jpg");

                // Este es el pedido que hay que hacer para ver una imagen en flickr, usando los datos recibidos de la consulta.
                //new LoadImage(deal).execute("https://www.flickr.com/photos/" + flickrObject.getPhoto().get(0).getId() + "/" + flickrObject.getPhoto().get(0).getOwner() + "/");

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
                int count = 0;
                while (i != -1) {
                    if (count >= 14)
                        outputStream.write(i);
                    i = inputStream.read();
                    count ++;
                }
                String str = outputStream.toString();
                if (str != null && str.length() > 0 && str.charAt(str.length()-1)=='x') {
                    str = str.substring(0, str.length()-1);
                }
                return str;
            } catch (IOException e) {
                return "";
            }
        }
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

    public class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private Deal deal;

        public LoadImage(Deal deal) {
            this.deal = deal;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                return BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                deal.setImage(result);
            } else {
                Toast.makeText(OffersResultActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }

}