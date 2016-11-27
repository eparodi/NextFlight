package com.example.martin.nextflight;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.martin.nextflight.elements.Rating;
import com.example.martin.nextflight.managers.ScreenUtility;

public class SingleReviewAcitvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        final ScreenUtility screenUtility = new ScreenUtility(this);

        Rating rating = (Rating) getIntent().getSerializableExtra("complete_rating");
        String yes_recommend = (String) getIntent().getSerializableExtra("yes_recommend");
        String comments = (String) getIntent().getSerializableExtra("comments");
        if (yes_recommend.equals("true"))
            yes_recommend = "Si";
        else if (yes_recommend.equals("false"))
            yes_recommend = "No";

        TextView comments_text = (TextView)findViewById(R.id.single_review_comment_text);
        comments_text.setText(comments);
        comments_text.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (screenUtility.getWidth() > 700.0) {
            ((RatingBar)findViewById(R.id.single_review_overall_bar)).setNumStars(10);
            ((RatingBar)findViewById(R.id.single_review_overall_bar)).setRating((rating.getOverall()).intValue());

        }
        ((RatingBar)findViewById(R.id.single_review_friendliness_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_food_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_punctuality_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_mileage_program_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_comfort_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_quality_price_bar)).setNumStars(10);

        ((RatingBar)findViewById(R.id.single_review_friendliness_bar)).setRating(rating.getFriendliness());
        ((RatingBar)findViewById(R.id.single_review_food_bar)).setRating(rating.getFood());
        ((RatingBar)findViewById(R.id.single_review_punctuality_bar)).setRating(rating.getPunctuality());
        ((RatingBar)findViewById(R.id.single_review_mileage_program_bar)).setRating(rating.getMileage_program());
        ((RatingBar)findViewById(R.id.single_review_comfort_bar)).setRating(rating.getComfort());
        ((RatingBar)findViewById(R.id.single_review_quality_price_bar)).setRating(rating.getQuality_price());

        TextView yes_recommend_text_view = (TextView)findViewById(R.id.single_review_yes_recommend);

        yes_recommend_text_view.setText(yes_recommend);
        if (yes_recommend.equals("Si"))
            yes_recommend_text_view.setTextColor(getResources().getColor(R.color.md_green_700));
        else
            yes_recommend_text_view.setTextColor(getResources().getColor(R.color.md_red_800));

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
