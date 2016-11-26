package com.example.martin.nextflight.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.martin.nextflight.FlightStatusActivity;
import com.example.martin.nextflight.MainActivity;
import com.example.martin.nextflight.R;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.elements.Status;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    private List<Flight> changed_flights = new ArrayList<>();
    private final static String NOTIF_KEY = "FLIGHT_STATUS";
    private final static int STACK_NOTIF_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        FileManager.startFileManager(context);
        ArrayList<Flight> activeFlights = FileManager.getAllFlights();
        for( Flight f : activeFlights ){
            new HttpGetStatus(context,f,intent).execute();
        }
    }

    private class HttpGetStatus extends AsyncTask<Void, Void, String> {

        private Context context;
        private Flight flight;
        private Intent intent;
        private static final String STATUS = "status";
        private String query;

        HttpGetStatus(Context context, Flight f,Intent intent){
            this.context = context;
            this.flight = f;
            this.intent = intent;
            this.query = "&airline_id=" + f.getAirline().getAirlineId() +
                    "&flight_number=" + f.getFlight_number();
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
                exception.printStackTrace();
                return context.getResources().getString(R.string.error);
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

                com.example.martin.nextflight.elements.Status status =
                        gson.fromJson(jsonFragment, com.example.martin.nextflight.elements.Status.class);

                notifManager(status);
            }catch(Exception e){

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

        private void notifManager(com.example.martin.nextflight.elements.Status status){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // TODO: Stack notifications.
            if (status.getStatus().equals(flight.getStatus())){
                Intent flightIntent = new Intent(context, FlightStatusActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(flightIntent);
                flightIntent.putExtra("AirlineId",flight.getAirline().getAirlineId());
                flightIntent.putExtra("FlightNumber",flight.getFlight_number().toString());

                flight.setArrival(status.getArrival());
                flight.setDeparture(status.getDeparture());
                flight.setStatus(status.getStatus());

                if (!changed_flights.contains(flight)){
                    changed_flights.add(flight);
                }

                String title;
                String content;
                Notification.Builder notification;
                final PendingIntent contentIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                title = "Vuelo " + flight.getFlight_number() + " de " + flight.getAirline().getAirlineName();
                content = "Estado: ";
                if (status.getStatus().equals("S")){
                    content +="Programado";
                }else if (status.getStatus().equals("L")){
                    content +="Aterrizado";
                }else if (status.getStatus().equals("A")){
                    content +="Activo";
                }else if (status.getStatus().equals("C")){
                    content +="Cancelado";
                }else if (status.getStatus().equals("R")){
                    content +="Desviado";
                }
                // Create the pending intent granting the Operating System to launch activity
                // when notification in action bar is clicked.
                notification = new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setSmallIcon(R.drawable.ic_flight)
                        .setGroup(NOTIF_KEY)
                        .setContentIntent(contentIntent);
                // Ignore deprecated warning. In newer devices SDK 16+ should use build() method.
                // getNotification() method internally calls build() method.
                notificationManager.notify(flight.getFlight_number(), notification.build());
            }

        }
    }

}
