package com.example.martin.nextflight;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Martin on 17/11/2016.
 */

public class FlightArrayAdapter extends ArrayAdapter<Flight> {
    public FlightArrayAdapter(Activity context, Flight[] objects) {
        super(context, R.layout.flight_list_view_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.flight_list_view_item, parent, false);
            holder = new ViewHolder();
            holder.flight_state_text_view = (TextView) convertView.findViewById(R.id.flight_list_state_text);
            holder.flight_airline_id_text_view = (TextView) convertView.findViewById(R.id.flight_list_airline_id_text);
            holder.flight_number_text_view = (TextView) convertView.findViewById(R.id.flight_list_number_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Flight flight = getItem(position);
        holder.flight_state_text_view.setText(flight.getState());
        holder.flight_airline_id_text_view.setText(flight.getAirline_id());
        Integer flight_number = flight.getNumber();
        holder.flight_number_text_view.setText(flight_number.toString());

        return convertView;
    }
}
