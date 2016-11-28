package com.example.martin.nextflight;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
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
import android.view.Menu;
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
import com.example.martin.nextflight.managers.ScreenUtility;
import com.google.android.gms.vision.text.Text;
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

    ImageView imageStatus;
    ImageView imageAirline;

    TextView textStatus;
    TextView textFlightNumber;
    TextView textAirline;

    TextView textFrom;
    TextView textTo;

    TextView textDepartureDate;
    TextView textDepartureHour;
    TextView textDepartureTerminal;
    TextView textDepartureGate;

    TextView textArrivalDate;
    TextView textArrivalHour;
    TextView textArrivalTerminal;
    TextView textArrivalGate;

    String flightNumber;

    Menu status_menu;

    ScreenUtility screen_utility;

    private boolean reload;

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

        screen_utility = new ScreenUtility(this);

        imageStatus = (ImageView) findViewById(R.id.image_status);
        imageAirline = (ImageView) findViewById(R.id.airline_image);

        textStatus = (TextView) findViewById(R.id.status_state_text_view);
        textFlightNumber = (TextView) findViewById(R.id.status_flight_number_text_view);
        textAirline = (TextView) findViewById(R.id.status_airline_name_text_view);

        textFrom = (TextView) findViewById(R.id.status_from_text_view);
        textTo = (TextView) findViewById(R.id.status_to_text_view);

        textDepartureDate = (TextView) findViewById(R.id.status_departure_date_text_view);
        textDepartureHour = (TextView) findViewById(R.id.status_departure_hour_text_view);
        textDepartureTerminal = (TextView) findViewById(R.id.status_departure_terminal_text_view);
        textDepartureGate = (TextView) findViewById(R.id.status_departure_gate_text_view);

        textArrivalDate = (TextView) findViewById(R.id.status_arrival_date_text_view);
        textArrivalHour = (TextView) findViewById(R.id.status_arrival_hour_text_view);
        textArrivalTerminal = (TextView) findViewById(R.id.status_arrival_terminal_text_view);
        textArrivalGate = (TextView) findViewById(R.id.status_arrival_gate_text_view);

        Bundle bundle = getIntent().getExtras();

        final String airline_id = bundle.getString("AirlineId");
        flightNumber = bundle.getString("FlightNumber");
        reload = bundle.getBoolean("RELOAD");

        new HttpGetStatus(airline_id,flightNumber).execute();

        // Clear notifications.
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(Integer.parseInt(flightNumber,10));
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
        }else if (status.equals("R")){
            textStatus.setText("Desviado");
            imageStatus.setImageResource(R.drawable.ic_avatar_delayed);
        }

        Airline airline = currentStatus.getAirline();
        textAirline.setText(airline.getAirlineName());
        textFlightNumber.setText(flightNumber);

        new DownloadImageTask(imageAirline).execute(airline.getLogo());

        Departure departure = currentStatus.getDeparture();
        Arrival arrival = currentStatus.getArrival();

        textFrom.setText(departure.getAirport().getDescription());
        textTo.setText(arrival.getAirport().getDescription());

        // In Android page, the API level is 1...
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date departure_date;
        Date arrival_date;

        try {
            departure_date = parser.parse(departure.getScheduled_time());
            textDepartureDate.setText(dateFormat.format(departure_date));
            textDepartureHour.setText(timeFormat.format(departure_date));

            arrival_date = parser.parse(arrival.getScheduled_time());
            textArrivalDate.setText(dateFormat.format(arrival_date));
            textArrivalHour.setText(timeFormat.format(arrival_date));
        }catch (Exception e){
            // TODO: Message.
        }

        String terminal = departure.getAirport().getTerminal();
        if (terminal == null){
            if (screen_utility.getWidth() > 700.0) {
                textDepartureTerminal.setText(R.string.null_information);
            }else {
                textDepartureTerminal.setText("--");
            }
            textDepartureTerminal.setTextColor(getResources().getColor(R.color.md_purple_400));
        }else{
            textDepartureTerminal.setText(terminal);
        }
        String gate = departure.getAirport().getGate();
        if (gate == null) {
            if (screen_utility.getWidth() > 700.0) {
                textDepartureGate.setText(R.string.null_information);
            }else {
                textDepartureGate.setText("--");
            }
            textDepartureGate.setTextColor(getResources().getColor(R.color.md_purple_400));
        } else{
            textDepartureGate.setText(gate);
        }

        terminal = arrival.getAirport().getTerminal();
        if (terminal == null){
            if (screen_utility.getWidth() > 700.0) {
                textArrivalTerminal.setText(R.string.null_information);
            }else {
                textArrivalTerminal.setText("--");
            }
            textArrivalTerminal.setTextColor(getResources().getColor(R.color.md_purple_400));
        }else{
            textArrivalTerminal.setText(terminal);
        }
        gate = arrival.getAirport().getGate();
        if (gate == null) {
            if (screen_utility.getWidth() > 700.0) {
                textArrivalGate.setText(R.string.null_information);
            }else {
                textArrivalGate.setText("--");
            }
            textArrivalGate.setTextColor(getResources().getColor(R.color.md_purple_400));
        } else{
            textArrivalGate.setText(gate);
        }

    }

    private void hideAll(){
        ((TextView) findViewById(R.id.status_state_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_flight_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_airline_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_from_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_departure_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_departure_date_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_departure_terminal_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_departure_hour_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_departure_gate_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_arrival_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_arrival_date_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_arrival_terminal_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_arrival_hour_title_text_view)).setText("");
        ((TextView) findViewById(R.id.status_arrival_gate_title_text_view)).setText("");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!reload){
                    finish();
                    return true;
                }
                break;
            case R.id.action_reviews_action_icon:
                Intent review_intent = new Intent(getApplicationContext(), ReviewActivity.class);

                PendingIntent review_pendingIntent =
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(review_intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder review_builder = new NotificationCompat.Builder(getApplicationContext());
                review_builder.setContentIntent(review_pendingIntent);

                review_intent.putExtra("FlightNumber", currentStatus.getNumber());
                review_intent.putExtra("AirlineId", currentStatus.getAirline().getAirlineId());
                review_intent.putExtra("AirlineName", currentStatus.getAirline().getAirlineName());

                startActivity(review_intent);
                return true;
            case R.id.action_favorite_action_icon:
                if (currentStatus!= null){
                    Flight f = new Flight(currentStatus.getNumber(),currentStatus.getAirline(),
                            currentStatus.getStatus(),currentStatus.getArrival(),currentStatus.getDeparture());
                    FileManager.startFileManager(getApplicationContext());
                    if (FileManager.checkFlight(f)){
                        item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                        item.setTitle(getResources().getString(R.string.action_favorite));
                        FileManager.removeFlight(f,getApplicationContext());
                    }else{
                        item.setIcon(R.drawable.ic_favorite_black);
                        item.setTitle(getResources().getString(R.string.action_favorite_forget));
                        FileManager.addFlight(f,getApplicationContext());
                    }
                }
                return true;
            case R.id.action_status_map_action_icon:
                Intent map_intent = new Intent(getApplicationContext(), StatusMapsActivity.class);

                PendingIntent map_pendingIntent =
                        TaskStackBuilder.create(getApplicationContext())
                                .addNextIntentWithParentStack(map_intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder map_builder = new NotificationCompat.Builder(getApplicationContext());
                map_builder.setContentIntent(map_pendingIntent);

                map_intent.putExtra("DEPLAT", currentStatus.getDeparture().getAirport().getLatitude());
                map_intent.putExtra("DEPLONG", currentStatus.getDeparture().getAirport().getLongitude());
                map_intent.putExtra("DEPMARK",currentStatus.getDeparture().getAirport().getDescription());
                map_intent.putExtra("ARVLAT", currentStatus.getArrival().getAirport().getLatitude());
                map_intent.putExtra("ARVLONG", currentStatus.getArrival().getAirport().getLongitude());
                map_intent.putExtra("ARVMARK",currentStatus.getArrival().getAirport().getDescription());

                /*
                    Bundle bundle = new Bundle();
                    bundle.putString("FlightNumber", flight_number);
                    bundle.putString("AirlineId", airline_id);

                    intent.putExtras(bundle); */

                startActivity(map_intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.flight_status, menu);
        status_menu = menu;
        return true;
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
                Flight f = new Flight(currentStatus.getNumber(),currentStatus.getAirline(),
                        currentStatus.getStatus(),currentStatus.getArrival(),currentStatus.getDeparture());
                FileManager.startFileManager(getApplicationContext());
                MenuItem favorite_item = status_menu.findItem(R.id.action_favorite_action_icon);
                if (FileManager.checkFlight(f)){
                    favorite_item.setIcon(R.drawable.ic_favorite_black);
                    favorite_item.setTitle(getResources().getString(R.string.action_favorite_forget));
                }else{
                    favorite_item.setIcon(R.drawable.ic_favorite_border_black_24dp);
                    favorite_item.setTitle(getResources().getString(R.string.action_favorite));
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        R.string.flight_unknown,
                        Toast.LENGTH_SHORT).show();
                //hideAll();
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
