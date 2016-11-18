package com.example.martin.nextflight.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.martin.nextflight.R;
import com.example.martin.nextflight.holders.FlightViewHolder;
import com.example.martin.nextflight.holders.ReviewViewHolder;

/**
 * Created by Martin on 18/11/2016.
 */

public class CommentsArrayAdapter extends ArrayAdapter<String> {

    public CommentsArrayAdapter(Activity context, String[] objects) {
        super(context, R.layout.review_list_view_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_view_item, parent, false);
            holder = new ReviewViewHolder();
            holder.review_comment_item_text = (TextView) convertView.findViewById(R.id.review_comment_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ReviewViewHolder) convertView.getTag();
        }

        String comment = getItem(position);
        holder.review_comment_item_text.setText(comment);
        return convertView;
    }

}
