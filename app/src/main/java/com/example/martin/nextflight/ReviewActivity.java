package com.example.martin.nextflight;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import com.example.martin.nextflight.adapters.CommentsArrayAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.example.martin.nextflight.elements.Review;

public class ReviewActivity extends AppCompatActivity {

    ReviewActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        context = this;

        TextView review_flight_number = (TextView)findViewById(R.id.review_flight_number_text_view);
        TextView review_airline_name = (TextView)findViewById(R.id.review_flight_airline_text_view);

        review_flight_number.setText("Vuelo " + "5620");
        review_airline_name.setText("Aerolìnea " + "Air Canada");

        new HttpGetReviews().execute();
    }

    private class HttpGetReviews extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/review.groovy?method=getairlinereviews");
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

            try {
                JSONObject obj = new JSONObject(result);
            } catch (Exception exception) {
                Toast.makeText(context, getString(R.string.error3), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject obj = new JSONObject(result);
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Review>>() {
                }.getType();

                String jsonFragment = obj.getString("reviews");
                ArrayList<Review> review_list = gson.fromJson(jsonFragment, listType);

                ArrayAdapter<String> resultList = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1);

                for (Review review : review_list) {
                    if (review.getComments() != null)
                        resultList.add(review.getComments());
                }

                ListView listView = (ListView) findViewById(R.id.review_comments_list_view);
                if (listView != null) {
                    listView.setAdapter(resultList);
                }
            } catch (Exception exception) {
                Toast.makeText(context, getString(R.string.error2), Toast.LENGTH_LONG).show();
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
