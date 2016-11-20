package com.example.martin.nextflight;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.elements.Status;
import com.example.martin.nextflight.status_tab.MapFlightFragment;
import com.example.martin.nextflight.status_tab.StatusFragment;
import com.example.martin.nextflight.status_tab.StatusTabFragmentPagerAdapter;
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

public class FlightStatusActivity extends AppCompatActivity
    implements StatusFragment.OnFragmentInteractionListener,
        MapFlightFragment.OnFragmentInteractionListener{

    // TODO: Change this when it is not useful anymore.
    Status currentStatus;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_status);

        viewPager = (ViewPager) findViewById(R.id.status_tabs_view);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.status_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Bundle bundle = getIntent().getExtras();

        String airline_id = bundle.getString("AirlineId");
        String flight_number = bundle.getString("FlightNumber");

        new HttpGetStatus(airline_id,flight_number).execute();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // EMPTY.
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

                viewPager.setAdapter(new StatusTabFragmentPagerAdapter(getSupportFragmentManager(),
                        FlightStatusActivity.this,result));
            } catch (Exception e) {

                // TODO: Add a message.
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
}
