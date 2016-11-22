package com.example.martin.nextflight;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.martin.nextflight.elements.Rating;

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

        Rating rating = (Rating) getIntent().getSerializableExtra("complete_rating");
        String yes_recommend = (String) getIntent().getSerializableExtra("yes_recommend");
        if (yes_recommend.equals("true"))
            yes_recommend = "Si";
        else if (yes_recommend.equals("false"))
            yes_recommend = "No";

        ((TextView)findViewById(R.id.single_review_overall_title)).setText(R.string.rating_overall_title_name);
        ((TextView)findViewById(R.id.single_review_friendliness_title)).setText(R.string.rating_friendliness_title_name);
        ((TextView)findViewById(R.id.single_review_food_title)).setText(R.string.rating_food_title_name);
        ((TextView)findViewById(R.id.single_review_punctuality_title)).setText(R.string.rating_punctuality_title_name);
        ((TextView)findViewById(R.id.single_review_mileage_program_title)).setText(R.string.rating_mileage_program_title_name);
        ((TextView)findViewById(R.id.single_review_comfort_title)).setText(R.string.rating_comfort_title_name);
        ((TextView)findViewById(R.id.single_review_quality_price_title)).setText(R.string.rating_quality_price_title_name);
        ((TextView)findViewById(R.id.single_review_yes_recommend_title)).setText(R.string.rating_yes_recommend_title_name);

        ((RatingBar)findViewById(R.id.single_review_overall_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_friendliness_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_food_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_punctuality_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_mileage_program_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_comfort_bar)).setNumStars(10);
        ((RatingBar)findViewById(R.id.single_review_quality_price_bar)).setNumStars(10);

        ((RatingBar)findViewById(R.id.single_review_overall_bar)).setRating((rating.getOverall()).intValue());
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
}
