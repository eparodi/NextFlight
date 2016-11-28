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
import android.support.v4.app.NotificationCompat;
import android.text.LoginFilter;
import android.util.Log;

import com.example.martin.nextflight.FlightStatusActivity;
import com.example.martin.nextflight.FollowedFlightsActivity;
import com.example.martin.nextflight.MainActivity;
import com.example.martin.nextflight.R;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.elements.Status;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmNotificationReceiver extends BroadcastReceiver {

    private ArrayList<Flight> changed_flights = new ArrayList<>();
    private final static String NOTIF_KEY = "FLIGHT_STATUS";
    private final static int STACK_NOTIF_ID = 0;
    private Object lock1 = new Object();

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
        private NotifHolder notif;

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

        private boolean hasChange(com.example.martin.nextflight.elements.Status status){
            return !status.getStatus().equals(flight.getStatus()) ||
                    !status.getArrival().getScheduled_time().equals(flight.getArrival().getScheduled_time()) ||
                    !status.getArrival().getAirport().getTerminal().equals(flight.getArrival().getAirport().getTerminal()) ||
                    !status.getArrival().getAirport().getGate().equals(flight.getArrival().getAirport().getGate()) ||
                    !status.getArrival().getAirport().getBaggage().equals(flight.getArrival().getAirport().getBaggage()) ||
                    !status.getDeparture().getScheduled_time().equals(flight.getDeparture().getScheduled_time()) ||
                    !status.getDeparture().getAirport().getTerminal().equals(flight.getDeparture().getAirport().getTerminal()) ||
                    !status.getDeparture().getAirport().getGate().equals(flight.getDeparture().getAirport().getGate());
        }

        private void notifManager(com.example.martin.nextflight.elements.Status status){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // TODO: Make prettier the notification.
            synchronized (lock1) {
                if (hasChange(status)) {
                    FileManager.startFileManager(context);
                    FileManager.removeFlight(flight, context);
                    flight.setArrival(status.getArrival());
                    flight.setDeparture(status.getDeparture());
                    flight.setStatus(status.getStatus());
                    FileManager.addFlight(flight, context);
                    if (!changed_flights.contains(flight)) {
                        changed_flights.add(flight);
                    }
                    if (changed_flights.size() == 1) {
                        if (notif == null){
                            notif = new NotifHolder();
                        }
                        Intent flightIntent = new Intent(context, FlightStatusActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(flightIntent);
                        flightIntent.putExtra("AirlineId", flight.getAirline().getAirlineId());
                        flightIntent.putExtra("FlightNumber", flight.getFlight_number().toString());
                        flightIntent.putExtra("Reload", true);

                        String title;
                        String content;
                        Notification.Builder notification;
                        final PendingIntent contentIntent =
                                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        title = "Vuelo " + flight.getFlight_number() + " de " + flight.getAirline().getAirlineName();
                        notif.createText(status,flight);
                        // Create the pending intent granting the Operating System to launch activity
                        // when notification in action bar is clicked.
                        notification = new Notification.Builder(context)
                                .setContentTitle(title)
                                .setContentText(notif.text)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setSmallIcon(R.drawable.ic_flight)
                                .setGroup(NOTIF_KEY)
                                .setContentIntent(contentIntent);
                        // Ignore deprecated warning. In newer devices SDK 16+ should use build() method.
                        // getNotification() method internally calls build() method.
                        notificationManager.notify(flight.getFlight_number(), notification.build());
                    } else {
                        notif = null;
                        Intent flightIntent = new Intent(context, FollowedFlightsActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(flightIntent);
                        flightIntent.putExtra("CHANGED_FLIGHTS", changed_flights);

                        String title;
                        NotificationCompat.Builder notification;
                        final PendingIntent contentIntent =
                                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        title = changed_flights.size() + " vuelos han cambiado su Estado.";
                        notification = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_flight)
                                .setGroup(NOTIF_KEY)
                                .setGroupSummary(true)
                                .setContentIntent(contentIntent);
                        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                        style.setBigContentTitle(title);
                        style.setSummaryText("NextFlight");
                        for (Flight f : changed_flights) {
                            style.addLine("Vuelo:" + f.getFlight_number());
                        }
                        notification.setStyle(style);
                        notificationManager.cancelAll();
                        notificationManager.notify(STACK_NOTIF_ID, notification.build());
                    }
                }
            }
        }
    }

    private class NotifHolder{
        protected String text;
        /*
        private String[] text_creator = new String[7];
        private boolean arrival_scheduled_time;
        private boolean departure_scheduled_time;
        private boolean arrival_terminal;
        private boolean departure_terminal;
        private boolean arrival_gate;
        private boolean departure_gate;
        private boolean baggage;*/

        protected void createText(Status status, Flight flight){
            text = "";
            if (!status.getStatus().equals(flight.getStatus())){
                text += "Estado: ";
                if (status.getStatus().equals("S")) {
                    text += "Programado";
                } else if (status.getStatus().equals("L")) {
                    text += "Aterrizado";
                } else if (status.getStatus().equals("A")) {
                    text += "Activo";
                } else if (status.getStatus().equals("C")) {
                    text += "Cancelado";
                } else if (status.getStatus().equals("R")) {
                    text += "Desviado";
                }
                text += "\n";
            }

            if (!status.getArrival().getAirport().getBaggage().equals(flight.getArrival().getAirport().getBaggage())){
                text += "Retiro de equipaje: " + status.getArrival().getAirport().getBaggage() + "\n";
            }

            if(!status.getDeparture().getScheduled_time().equals(flight.getDeparture().getScheduled_time())) {
                text += "Horario de partida: " + status.getDeparture().getScheduled_time() + "\n";
            }

            if (!status.getArrival().getScheduled_time().equals(flight.getArrival().getScheduled_time())){
                text += "Horario de arribo: " + status.getArrival().getAirport().getBaggage() + "\n";
            }

            if(!status.getDeparture().getAirport().getTerminal().equals(flight.getDeparture().getAirport().getTerminal())) {
                text += "Terminal de partida: " + status.getArrival().getAirport().getTerminal() + "\n";
            }

            if(!status.getDeparture().getAirport().getGate().equals(flight.getDeparture().getAirport().getGate())){
                text += "Puerta de partida: " + status.getArrival().getAirport().getTerminal() + "\n";
            }

            if (!status.getArrival().getAirport().getTerminal().equals(flight.getArrival().getAirport().getTerminal())){
                text += "Terminal de arribo: " + status.getArrival().getAirport().getTerminal() + "\n";
            }

            if (!status.getArrival().getAirport().getGate().equals(flight.getArrival().getAirport().getGate())){
                text += "Puerta de arribo: " + status.getArrival().getAirport().getTerminal() + "\n";
            }
        }
    }
}
