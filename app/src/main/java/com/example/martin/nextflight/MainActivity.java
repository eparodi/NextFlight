package com.example.martin.nextflight;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ListView;

import com.example.martin.nextflight.adapters.FlightArrayAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        } else if (id == R.id.nav_offers) {

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

    private class HttpGetTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/geo.groovy?method=getcountries");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception exception) {
                exception.printStackTrace();
                return getResources().getString(R.string.error);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            String json = "{\"meta\":{\"uuid\":\"d26efa4a-6de6-447c-aaa7-f1a4bf62ba4b\",\"time\":\"102.224ms\"},\"page\":1,\"page_size\":30,\"total\":13,\"countries\":[{\"id\":\"AR\",\"name\":\"Argentina\",\"longitude\":-34.6,\"latitude\":-58.3833},{\"id\":\"PY\",\"name\":\"Paraguay\",\"longitude\":-25.2666,\"latitude\":-57.6666},{\"id\":\"ES\",\"name\":\"Espa&#241;a\",\"longitude\":40.4166,\"latitude\":-3.75},{\"id\":\"CO\",\"name\":\"Colombia\",\"longitude\":4.5833,\"latitude\":-74.0666},{\"id\":\"MX\",\"name\":\"Mexico\",\"longitude\":19.05,\"latitude\":-99.3666},{\"id\":\"BR\",\"name\":\"Brasil\",\"longitude\":-15.7833,\"latitude\":-47.8666},{\"id\":\"US\",\"name\":\"Estados Unidos\",\"longitude\":38.8833,\"latitude\":-77.0333},{\"id\":\"PE\",\"name\":\"Peru\",\"longitude\":-12.0433,\"latitude\":-77.0283},{\"id\":\"UY\",\"name\":\"Uruguay\",\"longitude\":-34.8833,\"latitude\":-56.1666},{\"id\":\"IT\",\"name\":\"Italia\",\"longitude\":41.9,\"latitude\":12.4833},{\"id\":\"FR\",\"name\":\"Francia\",\"longitude\":48.8166,\"latitude\":2.4833},{\"id\":\"GB\",\"name\":\"Reino Unido\",\"longitude\":51.5,\"latitude\":-0.1166},{\"id\":\"CL\",\"name\":\"Chile\",\"longitude\":-70.5665,\"latitude\":-33.4254}]}";

            /*
            try {
                JSONObject obj = new JSONObject(json);
                if (!obj.has(MainActivity.COUNTRIES_NAME))
                    return;

                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Country>>() {
                }.getType();

                String jsonFragment = obj.getString(MainActivity.COUNTRIES_NAME);
                ArrayList<Country> countryList = gson.fromJson(jsonFragment, listType);

                ListView listView = (ListView) findViewById(R.id.listView);
                if (listView != null) {
                    ArrayAdapter<Country> arrarAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countryList);
                    listView.setAdapter(arrarAdapter);
                }
            } catch (Exception exception) {
                Toast.makeText(this, getString(R.string.toast_message), Toast.LENGTH_LONG).show();
            }
            */
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
