package com.example.martin.nextflight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.adapters.FlightArrayAdapter;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.managers.AlarmNotificationReceiver;
import com.example.martin.nextflight.managers.FileManager;
import com.example.martin.nextflight.managers.ScreenUtility;
import com.example.martin.nextflight.managers.SettingsManager;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FlightArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent alarmNotificationReceiverIntent =
                new Intent(MainActivity.this, AlarmNotificationReceiver.class);
        PendingIntent alarmNotificationReceiverPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmNotificationReceiverIntent, 0);

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

        final ListView view = (ListView) findViewById(R.id.followed_flights_list_view);
        final ScreenUtility screenUtility = new ScreenUtility(this);

        SettingsManager.startSettingsManager(getApplicationContext());

        FileManager.startFileManager(getApplicationContext());
        final ArrayList<Flight> flights = FileManager.getAllFlights();

        final FlightArrayAdapter flights_adapter = new FlightArrayAdapter(this, flights, screenUtility);
        view.setAdapter(flights_adapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FlightStatusActivity.class);
                intent.putExtra("FlightNumber", flights.get(position).getFlight_number().toString());
                intent.putExtra("AirlineId", flights.get(position).getAirline().getAirlineId());
                intent.putExtra("Reload",false);

                // Use TaskStackBuilder to build the back stack and get the PendingIntent
                PendingIntent pendingIntent =
                        TaskStackBuilder.create(getApplicationContext())
                                // add all of DetailsActivity's parents to the stack,
                                // followed by DetailsActivity itself
                                .addNextIntentWithParentStack(intent)
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setContentIntent(pendingIntent);

                startActivity(intent);
            }
        });

        if (flights.isEmpty()) {
            ((TextView) findViewById(R.id.no_followed_flights)).setText(R.string.no_followed_flights_information);
            ((TextView) findViewById(R.id.main_help_search_title)).setText(R.string.help_search_flight_title);
            ((ImageView) findViewById(R.id.help_search_icon_phone)).setImageResource(R.mipmap.help_search_icon_phone);
            ((TextView) findViewById(R.id.main_help_offers_title)).setText(R.string.help_search_offers_title);
            ((ImageView) findViewById(R.id.help_offers_icon_phone)).setImageResource(R.mipmap.help_offers_phone);
            if (screenUtility.getWidth() > 700.0) {
                ((TextView) findViewById(R.id.main_help_flight_title)).setText(R.string.help_follow_flight_title);
                ((ImageView) findViewById(R.id.help_flight_icon_tablet)).setImageResource(R.mipmap.help_favorite_tablet);
            }
        }

        view.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            ArrayList<Flight> checked_flights = new ArrayList<>();
            ArrayList<Integer> checekd_indexes = new ArrayList<>();

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_delete) {
                    for (int index = 0; index < checked_flights.size(); index++) {
                        Flight flight = checked_flights.get(index);
                        FileManager.removeFlight(flight, getApplicationContext());
                        flights.remove(flight);
                    }
                    for (Integer index : checekd_indexes) {
                        if (index < flights.size()) {
                            view.getChildAt(index).setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    checked_flights = new ArrayList<>();
                    checekd_indexes = new ArrayList<>();
                    view.clearChoices();
                    flights_adapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                if (checked) {
                    checked_flights.add(flights.get(position));
                    checekd_indexes.add(position);
                    view.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.md_indigo_100));
                }else{
                    checked_flights.remove(flights.get(position));
                    checekd_indexes.remove((Integer) position);
                    view.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
        view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        adapter = flights_adapter;
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
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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

        if (id == R.id.nav_offers) {
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

            Intent intent = new Intent(this,SettingsActivity.class);

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

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(this,HelpActivity.class);

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
