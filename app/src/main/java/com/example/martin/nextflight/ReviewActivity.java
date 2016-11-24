package com.example.martin.nextflight;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        Bundle bundle = getIntent().getExtras();

        final String airline_id = bundle.getString("AirlineId");
        final String flight_number = bundle.getString("FlightNumber");
        final String airline_name = bundle.getString("AirlineName");

        TextView review_flight_number = (TextView)findViewById(R.id.review_flight_number_text_view);
        TextView review_airline_name = (TextView)findViewById(R.id.review_flight_airline_text_view);

        review_flight_number.setText(flight_number);
        review_flight_number.setTextColor(getResources().getColor(R.color.md_blue_400));
        review_airline_name.setText(airline_name);
        review_airline_name.setTextColor(getResources().getColor(R.color.md_blue_400));

        new HttpGetReviews(airline_id,flight_number).execute();

        FloatingActionButton add_button = (FloatingActionButton) findViewById(R.id.review_add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, SubmitReviewActivity.class);

                intent.putExtra("FlightNumber", flight_number);
                intent.putExtra("AirlineId", airline_id);

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

    private class HttpGetReviews extends AsyncTask<Void, Void, String> {

        private String query;

        HttpGetReviews(String id, String number){
            query = "&airline_id="+id+"&flight_number"+number;
        }
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://hci.it.itba.edu.ar/v1/api/review.groovy?method=getairlinereviews"+query);
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

                ArrayAdapter<String> result_list = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1);

                getComments(result_list, review_list);
                Double overall = getOverallRating(review_list);

                TextView overall_rating = (TextView)findViewById(R.id.review_general_rating);
                overall_rating.setText(overall.toString());
                overall_rating.setTextColor(getResources().getColor(getOverallColor(overall)));

                ListView listView = (ListView) findViewById(R.id.review_comments_list_view);
                if (listView != null) {
                    listView.setAdapter(result_list);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(context,SingleReviewAcitvity.class);
                            intent.putExtra("complete_rating", review_list.get(position).getRating());
                            intent.putExtra("yes_recommend", review_list.get(position).getYes_recommend());

                            // Use TaskStackBuilder to build the back stack and get the PendingIntent
                            PendingIntent pendingIntent =
                                    TaskStackBuilder.create(context)
                                            // add all of DetailsActivity's parents to the stack,
                                            // followed by DetailsActivity itself
                                            .addNextIntentWithParentStack(intent)
                                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                            builder.setContentIntent(pendingIntent);

                            startActivity(intent);
                        }
                    });
                }
            } catch (Exception exception) {
                Toast.makeText(context, getString(R.string.error), Toast.LENGTH_LONG).show();
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
            String comment = review.getComments();
            if (comment != null)
                result_list.add(comment);
        }
    }

    public double getOverallRating(ArrayList<Review> review_list) {
        int overall = 0;
        for (Review review : review_list) {
            Double review_overall = review.getRating().getOverall();
            overall += review_overall;
        }
        double resp = (double)overall / review_list.size();
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
