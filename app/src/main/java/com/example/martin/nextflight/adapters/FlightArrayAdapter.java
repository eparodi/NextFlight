package com.example.martin.nextflight.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martin.nextflight.Flight;
import com.example.martin.nextflight.R;
import com.example.martin.nextflight.holders.FlightViewHolder;

/**
 * Created by Martin on 17/11/2016.
 */

public class FlightArrayAdapter extends ArrayAdapter<Flight> {
    public FlightArrayAdapter(Activity context, Flight[] objects) {
        super(context, R.layout.flight_list_view_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FlightViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.flight_list_view_item, parent, false);
            holder = new FlightViewHolder();
            holder.flight_state_image_view = (ImageView) convertView.findViewById(R.id.avatar_state_image_view);
            holder.flight_airline_id_text_view = (TextView) convertView.findViewById(R.id.flight_list_airline_id_text);
            holder.flight_number_text_view = (TextView) convertView.findViewById(R.id.flight_list_number_text);
            holder.flight_time_text_view = (TextView) convertView.findViewById(R.id.flight_list_time_text);
            holder.flight_date_text_view = (TextView) convertView.findViewById(R.id.flight_list_date_text);
            convertView.setTag(holder);
        } else {
            holder = (FlightViewHolder) convertView.getTag();
        }

        Flight flight = getItem(position);
        if (flight.getState().equals("A")) {
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_active);
        } else if(flight.getState().equals("C")) {
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_canceled);
        } else if(flight.getState().equals("D")) {
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_delayed);
        } else if(flight.getState().equals("L")) {
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_landed);
        } else if(flight.getState().equals("S")) {
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_programmed);
        }
        holder.flight_airline_id_text_view.setText(flight.getAirline_id());
        Integer flight_number = flight.getNumber();
        holder.flight_number_text_view.setText(flight_number.toString());
        holder.flight_time_text_view.setText(flight.getArrival_time());
        holder.flight_date_text_view.setText(flight.getArrival_date());

        return convertView;
    }
}
