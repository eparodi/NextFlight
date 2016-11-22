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
import android.util.Log;

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

public class AlarmNotificationReceiver extends BroadcastReceiver {

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
                Log.d("Hola","Hola");
                if (status.getStatus().equals(flight.getStatus())){
                    Log.d("Hola","Hola");
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(intent);
                    // Create the pending intent granting the Operating System to launch activity
                    // when notification in action bar is clicked.
                    final PendingIntent contentIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    flight.setArrival(status.getArrival());
                    flight.setDeparture(status.getDeparture());
                    flight.setStatus(status.getStatus());
                    Notification notification = new Notification.Builder(context)
                            .setContentTitle("TODO: Set Title")
                            .setContentText("TODO: Set Text and Icons.")
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.stat_sys_download_done))
                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                            .setContentIntent(contentIntent).build();
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    // Ignore deprecated warning. In newer devices SDK 16+ should use build() method.
                    // getNotification() method internally calls build() method.
                    notificationManager.notify(1, notification);
                }
            }catch(Exception e){
                Log.d("Hola",e.getMessage());
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
