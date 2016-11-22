package com.example.martin.nextflight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.managers.AlarmNotificationReceiver;
import com.example.martin.nextflight.managers.FileManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainActivity context;
    private AlarmManager alarmManager;
    private PendingIntent alarmNotificationReceiverPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent alarmNotificationReceiverIntent =
                new Intent(MainActivity.this, AlarmNotificationReceiver.class);
        alarmNotificationReceiverPendingIntent =
                PendingIntent.getBroadcast(MainActivity.this, 0, alarmNotificationReceiverIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 30000,
                30000,
                alarmNotificationReceiverPendingIntent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView view = (ListView) findViewById(R.id.followed_flights_list_view);

        FileManager.startFileManager(getApplicationContext());
        ArrayList<Flight> flights = FileManager.getAllFlights();
        ArrayAdapter<Flight> flightAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,flights);
        view.setAdapter(flightAdapter);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this,FlightSearchActivity.class);

            // Use TaskStackBuilder to build the back stack and get the PendingIntent
            PendingIntent pendingIntent =
                    TaskStackBuilder.create(this)
                            // add all of DetailsActivity's parents to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(intent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);

            startActivity(intent);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_flight) {
            // Current Activity.
        } else if (id == R.id.nav_offers) {
            Intent intent = new Intent(this,OffersSearchActivity.class);

            // Use TaskStackBuilder to build the back stack and get the PendingIntent
            PendingIntent pendingIntent =
                    TaskStackBuilder.create(this)
                            // add all of DetailsActivity's parents to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(intent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);

            startActivity(intent);
        } else if (id == R.id.nav_converter) {

            Intent intent = new Intent(this,ConverterActivity.class);

            // Use TaskStackBuilder to build the back stack and get the PendingIntent
            PendingIntent pendingIntent =
                    TaskStackBuilder.create(this)
                            // add all of DetailsActivity's parents to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(intent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);

            startActivity(intent);

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

            Intent intent = new Intent(this,ReviewActivity.class);

            // Use TaskStackBuilder to build the back stack and get the PendingIntent
            PendingIntent pendingIntent =
                    TaskStackBuilder.create(this)
                            // add all of DetailsActivity's parents to the stack,
                            // followed by DetailsActivity itself
                            .addNextIntentWithParentStack(intent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);

            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
