package com.example.martin.nextflight;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.elements.Airline;
import com.example.martin.nextflight.elements.Arrival;
import com.example.martin.nextflight.elements.Departure;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.elements.Status;
import com.example.martin.nextflight.managers.FileManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlightStatusActivity extends AppCompatActivity{

    Status currentStatus;
    TextView textStatus;
    ImageView imageStatus;
    TextView textAirline;
    ImageView imageAirline;
    TextView textFrom;
    TextView textTo;
    TextView textDeparture;
    TextView textArrival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_status);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar); */
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        textStatus = (TextView) findViewById(R.id.status);
        imageStatus = (ImageView) findViewById(R.id.image_status);
        textAirline = (TextView) findViewById(R.id.airlineName);
        imageAirline = (ImageView) findViewById(R.id.airline_image);
        textFrom = (TextView) findViewById(R.id.from_info);
        textTo = (TextView) findViewById(R.id.to_info);
        textArrival = (TextView) findViewById(R.id.arrival_info);
        textDeparture = (TextView) findViewById(R.id.departure_info);

        Bundle bundle = getIntent().getExtras();

        final String airline_id = bundle.getString("AirlineId");
        final String flight_number = bundle.getString("FlightNumber");

        new HttpGetStatus(airline_id,flight_number).execute();

        FloatingActionButton map_button = (FloatingActionButton) findViewById(R.id.map_button);
        map_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatusMapsActivity.class);

                PendingIntent pendingIntent =
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setContentIntent(pendingIntent);

                intent.putExtra("DEPLAT", currentStatus.getDeparture().getAirport().getLatitude());
                intent.putExtra("DEPLONG", currentStatus.getDeparture().getAirport().getLongitude());
                intent.putExtra("DEPMARK",currentStatus.getDeparture().getAirport().getDescription());
                intent.putExtra("ARVLAT", currentStatus.getArrival().getAirport().getLatitude());
                intent.putExtra("ARVLONG", currentStatus.getArrival().getAirport().getLongitude());
                intent.putExtra("ARVMARK",currentStatus.getArrival().getAirport().getDescription());

                /*
                    Bundle bundle = new Bundle();
                    bundle.putString("FlightNumber", flight_number);
                    bundle.putString("AirlineId", airline_id);

                    intent.putExtras(bundle); */

                startActivity(intent);
            }
        });

        FloatingActionButton review_button = (FloatingActionButton) findViewById(R.id.review_button);
        review_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);

                PendingIntent pendingIntent =
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setContentIntent(pendingIntent);

                intent.putExtra("FlightNumber", flight_number);
                intent.putExtra("AirlineId", airline_id);
                intent.putExtra("AirlineName", currentStatus.getAirline().getAirlineName());

                startActivity(intent);
            }
        });


    }

    private void fillTextView(){
        String status = currentStatus.getStatus();
        if (status.equals("S")){
            textStatus.setText("Programado");
            imageStatus.setImageResource(R.drawable.ic_avatar_programmed);
        }else if (status.equals("L")){
            textStatus.setText("Aterrizado");
            imageStatus.setImageResource(R.drawable.ic_avatar_landed);
        }else if (status.equals("A")){
            textStatus.setText("Activo");
            imageStatus.setImageResource(R.drawable.ic_avatar_active);
        }else if (status.equals("C")){
            textStatus.setText("Cancelado");
            imageStatus.setImageResource(R.drawable.ic_avatar_canceled);
        }else if (status.equals("D")){
            textStatus.setText("Desviado");
            imageStatus.setImageResource(R.drawable.ic_avatar_delayed);
        }

        Airline airline = currentStatus.getAirline();
        textAirline.setText(airline.getAirlineName());

        new DownloadImageTask(imageAirline).execute(airline.getLogo());

        Departure departure = currentStatus.getDeparture();
        Arrival arrival = currentStatus.getArrival();

        textFrom.setText(departure.getAirport().getDescription());
        textTo.setText(arrival.getAirport().getDescription());

        String departure_data = "";
        String arrival_data = "";

        // In Android page, the API level is 1...
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date departure_date;
        Date arrival_date;

        try {
            departure_date = parser.parse(departure.getScheduled_time());
            arrival_date = parser.parse(arrival.getScheduled_time());
            departure_data = dateFormat.format(departure_date) + " | " +
                    timeFormat.format(departure_date) + "\n";
            arrival_data = dateFormat.format(arrival_date) + " | " +
                    timeFormat.format(arrival_date) + "\n";
        }catch (Exception e){
            // TODO: Message.
        }

        departure_data += "Terminal: ";

        String terminal = departure.getAirport().getTerminal();
        if (terminal == null){
            departure_data +=  "No hay información disponible\n";
        }else{
            departure_data += terminal + "\n";
        }

        arrival_data += "Terminal: ";
        terminal = arrival.getAirport().getTerminal();
        if (terminal == null){
            arrival_data +=  "No hay información disponible\n";
        }else{
            arrival_data += terminal + "\n";
        }

        arrival_data += "Puerta: ";
        String puerta = departure.getAirport().getGate();
        if (puerta == null){
            arrival_data +=  "No hay información disponible\n";
        }else{
            arrival_data += puerta + "\n";
        }

        departure_data += "Puerta: ";
        puerta = arrival.getAirport().getGate();
        if (puerta == null){
            departure_data +=  "No hay información disponible\n";
        }else{
            departure_data += puerta + "\n";
        }

        textDeparture.setText(departure_data);
        textArrival.setText(arrival_data);

    }

    private void hideAll(){
        ((TextView) findViewById(R.id.status_title)).setText("");
        ((TextView) findViewById(R.id.from_title)).setText("");
        ((TextView) findViewById(R.id.to_title)).setText("");
        ((TextView) findViewById(R.id.arrival_title)).setText("");
        ((TextView) findViewById(R.id.departure_title)).setText("");
    }

    private void configurateFavButton(){
        FloatingActionButton fav_button = (FloatingActionButton) findViewById(R.id.favourite_button);
        Flight f = new Flight(currentStatus.getNumber(),currentStatus.getAirline(),
                currentStatus.getStatus(),currentStatus.getArrival(),currentStatus.getDeparture());
        FileManager.startFileManager(getApplicationContext());
        if (FileManager.checkFlight(f)){
            fav_button.setImageResource(R.drawable.ic_favorite_black);
        }else{
            fav_button.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        fav_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FloatingActionButton fav_button = (FloatingActionButton) findViewById(R.id.favourite_button);
                Flight f = new Flight(currentStatus.getNumber(),currentStatus.getAirline(),
                        currentStatus.getStatus(),currentStatus.getArrival(),currentStatus.getDeparture());
                if (FileManager.checkFlight(f)){
                    fav_button.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    FileManager.removeFlight(f,getApplicationContext());
                }else{
                    fav_button.setImageResource(R.drawable.ic_favorite_black);
                    FileManager.addFlight(f,getApplicationContext());
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class HttpGetStatus extends AsyncTask<Void, Void, String> {

        private static final String STATUS = "status";

        private String query;

        protected HttpGetStatus(String airline_id, String flight_number){
            super();
            try {
                this.query = "&airline_id=" + URLEncoder.encode(airline_id, "UTF-8") +
                        "&flight_number=" + URLEncoder.encode(flight_number, "UTF-8");
            }catch(Exception e){
                // TODO: Set messagges.
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus"+query);
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

                String jsonFragment = obj.getString(STATUS);

                currentStatus = gson.fromJson(jsonFragment, com.example.martin.nextflight.elements.Status.class);

                fillTextView();
                configurateFavButton();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        R.string.flight_unknown,
                        Toast.LENGTH_SHORT).show();
                hideAll();
                finish();
            } finally {

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(Bitmap.createScaledBitmap(result,100,60,false));
        }
    }

}
