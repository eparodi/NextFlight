package com.example.martin.nextflight;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.adapters.CommentsArrayAdapter;
import com.example.martin.nextflight.elements.Airline;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.elements.Rating;
import com.example.martin.nextflight.elements.SubmitReview;
import com.example.martin.nextflight.managers.ScreenUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.example.martin.nextflight.elements.Review;

public class ReviewActivity extends AppCompatActivity {

    private ReviewActivity context;
    private String AIRLINE_ID;
    private String FLIGHT_NUMBER;
    private String AIRLINE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        final ScreenUtility screenUtility = new ScreenUtility(this);

        Bundle bundle = getIntent().getExtras();

        FLIGHT_NUMBER = String.valueOf(bundle.getInt("FlightNumber"));
        AIRLINE_ID = bundle.getString("AirlineId");
        AIRLINE_NAME = bundle.getString("AirlineName");

        TextView review_flight_number = (TextView) findViewById(R.id.review_flight_number_text_view);
        TextView review_airline_name = (TextView) findViewById(R.id.review_flight_airline_text_view);

        review_flight_number.setText(FLIGHT_NUMBER);
        review_flight_number.setTextColor(getResources().getColor(R.color.md_blue_400));
        review_airline_name.setText(AIRLINE_NAME);
        review_airline_name.setTextColor(getResources().getColor(R.color.md_blue_400));

        new HttpGetReviews(AIRLINE_ID, FLIGHT_NUMBER).execute();

        if (screenUtility.getWidth() > 700.0) {
            Button button = (Button) findViewById(R.id.review_submit_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Integer flight_number = Integer.parseInt(FLIGHT_NUMBER);
                    String airline_id = AIRLINE_ID;

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
            });
        }
        else {
            FloatingActionButton add_button = (FloatingActionButton) findViewById(R.id.review_add_button);
            add_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ReviewActivity.this, SubmitReviewActivity.class);

                    intent.putExtra("FlightNumber", FLIGHT_NUMBER);
                    intent.putExtra("AirlineId", AIRLINE_ID);

                    PendingIntent pendingIntent =
                            TaskStackBuilder.create(getApplicationContext())
                                    .addNextIntentWithParentStack(intent)
                                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    builder.setContentIntent(pendingIntent);

                    startActivity(intent);
                }
            });
        }
    }

    private class HttpGetReviews extends AsyncTask<Void, Void, String> {

        private String query;

        HttpGetReviews(String id, String number) {
            query = "&airline_id=" + id + "&flight_number" + number;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/review.groovy?method=getairlinereviews" + query);
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
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Review>>() {
                }.getType();

                String jsonFragment = obj.getString("reviews");
                final ArrayList<Review> review_list = gson.fromJson(jsonFragment, listType);

                CommentsArrayAdapter result_list = new CommentsArrayAdapter(ReviewActivity.this,
                        new ArrayList<String>());

                getComments(result_list, review_list);
                Double overall = getOverallRating(review_list);

                TextView overall_rating = (TextView) findViewById(R.id.review_general_rating);
                overall_rating.setText(overall.toString());
                overall_rating.setTextColor(getResources().getColor(getOverallColor(overall)));

                ListView listView = (ListView) findViewById(R.id.review_comments_list_view);
                if (listView != null) {
                    listView.setAdapter(result_list);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(ReviewActivity.this, SingleReviewAcitvity.class);
                            intent.putExtra("comments", review_list.get(position).getComments());
                            intent.putExtra("complete_rating", review_list.get(position).getRating());
                            intent.putExtra("yes_recommend", review_list.get(position).getYes_recommend());

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
                }
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
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

    public void getComments(ArrayAdapter<String> result_list, ArrayList<Review> review_list) {
        for (Review review : review_list) {
            String comment_raw = review.getComments();
            try
            {
                String comment = URLDecoder.decode(comment_raw, "UTF-8");
                result_list.add(comment);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
    }

    public double getOverallRating(ArrayList<Review> review_list) {
        int overall = 0;
        for (Review review : review_list) {
            Double review_overall = review.getRating().getOverall();
            overall += review_overall;
        }
        double resp = (double) overall / review_list.size();
        resp = Math.floor(resp * 100) / 100;
        return resp;
    }

    public int getOverallColor(Double overall) {
        int[] colors = {
                R.color.md_red_A700,                // 1
                R.color.md_deep_orange_A700,        // 2
                R.color.md_orange_A700,             // 3
                R.color.md_amber_A700,              // 4
                R.color.md_yellow_A700,             // 5
                R.color.md_lime_A400,               // 6
                R.color.md_lime_A700,               // 7
                R.color.md_light_green_A700,        // 8
                R.color.md_green_A700,              // 9
                R.color.md_green_A700};             // 10
        return colors[overall.intValue() - 1];
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
                    Toast.makeText(getApplicationContext(), "Se subio con éxito", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Ocurrió un error", Toast.LENGTH_LONG).show();
            } catch (Exception exception) {
                Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_connection_error),
                        Toast.LENGTH_SHORT).show();
            }
            return query;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
