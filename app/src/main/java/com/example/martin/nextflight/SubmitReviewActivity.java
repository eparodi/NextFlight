package com.example.martin.nextflight;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.elements.Airline;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.elements.Rating;
import com.example.martin.nextflight.elements.SubmitReview;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.R.attr.id;
import static java.lang.System.in;

public class SubmitReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_review);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        ((TextView)findViewById(R.id.submit_review_friendliness_title)).setText(R.string.rating_friendliness_title_name);
        ((TextView)findViewById(R.id.submit_review_food_title)).setText(R.string.rating_food_title_name);
        ((TextView)findViewById(R.id.submit_review_punctuality_title)).setText(R.string.rating_punctuality_title_name);
        ((TextView)findViewById(R.id.submit_review_mileage_program_title)).setText(R.string.rating_mileage_program_title_name);
        ((TextView)findViewById(R.id.submit_review_comfort_title)).setText(R.string.rating_comfort_title_name);
        ((TextView)findViewById(R.id.submit_review_quality_price_title)).setText(R.string.rating_quality_price_title_name);
        ((TextView)findViewById(R.id.submit_switch)).setText(R.string.rating_yes_recommend_title_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submit_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            Integer flight_number = Integer.parseInt(getIntent().getStringExtra("FlightNumber"));
            String airline_id = getIntent().getStringExtra("AirlineId");

            Boolean yes_recommend = ((Switch)findViewById(R.id.submit_switch)).isChecked();
            Airline airline = new Airline(airline_id);
            Flight flight = new Flight(flight_number, airline);
            String comments = ((EditText)findViewById(R.id.submit_comment_input)).getText().toString();
            Rating rating = new Rating(
                    4.5,
                    ((SeekBar)findViewById(R.id.submit_review_friendliness_seek_bar)).getProgress() + 1,
                    ((SeekBar)findViewById(R.id.submit_review_food_seek_bar)).getProgress() + 1,
                    ((SeekBar)findViewById(R.id.submit_review_punctuality_seek_bar)).getProgress() + 1,
                    ((SeekBar)findViewById(R.id.submit_review_mileage_program_seek_bar)).getProgress() + 1,
                    ((SeekBar)findViewById(R.id.submit_review_comfort_seek_bar)).getProgress() + 1,
                    ((SeekBar)findViewById(R.id.submit_review_quality_price_seek_bar)).getProgress() + 1
            );
            SubmitReview submit = new SubmitReview(flight, rating, yes_recommend, comments);

            new HttpSubmitReview(submit).execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpSubmitReview extends AsyncTask<Void, Void, String> {

        private SubmitReview submit;

        HttpSubmitReview(SubmitReview submit){
            this.submit = submit;
        }
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/review.groovy?method=reviewairline2" + getQuery(submit));
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
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
                Gson gson = new Gson();
                Type resultType = new TypeToken<Boolean>() {
                }.getType();

                String jsonFragment = obj.getString("review");
                final Boolean review_result = gson.fromJson(jsonFragment, resultType);

                if (review_result == true) {
                    Toast.makeText(SubmitReviewActivity.this, "Se subio con éxito", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(SubmitReviewActivity.this, "Ocurrió un error", Toast.LENGTH_LONG).show();
            } catch (Exception exception) {
                Toast.makeText(SubmitReviewActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
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

        private String getQuery(SubmitReview submit) {
            String query = "";

            try {
                query = "&review=%7b%22flight%22:%7b%22airline%22:%7b%22id%22:%22" +
                        URLEncoder.encode(submit.getFlight().getAirline().getAirlineId(), "UTF-8") +
                        "%22%7d,%22number%22:" +
                        submit.getFlight().getFlight_number().toString() +
                        "%7d,%22rating%22:%7b%22friendliness%22:" +
                        submit.getRating().getFriendliness().toString() +
                        ",%22food%22:" +
                        submit.getRating().getFood().toString() +
                        ",%22punctuality%22:" +
                        submit.getRating().getPunctuality().toString() +
                        ",%22mileage_program%22:" +
                        submit.getRating().getMileage_program().toString() +
                        ",%22comfort%22:" +
                        submit.getRating().getComfort().toString() +
                        ",%22quality_price%22:" +
                        submit.getRating().getQuality_price().toString() +
                        "%7d,%22yes_recommend%22:" +
                        submit.getYes_recommend().toString() +
                        ",%22comments%22:%22" +
                        URLEncoder.encode(submit.getComments(), "UTF-8") +
                        "%22%7d";
                return query;
            } catch(Exception e) {
                // TODO: Set messagges.
            }
            return query;
        }
    }
}
