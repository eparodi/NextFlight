package com.example.martin.nextflight.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.R;
import com.example.martin.nextflight.elements.Departure;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.holders.FlightViewHolder;
import com.example.martin.nextflight.managers.ScreenUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Martin on 17/11/2016.
 */

public class FlightArrayAdapter extends ArrayAdapter<Flight> {

    private ScreenUtility screenUtility;

    public FlightArrayAdapter(Activity context, ArrayList<Flight> objects, ScreenUtility screen_utility) {
        super(context, R.layout.flight_list_view_item, objects);
        screenUtility = screen_utility;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FlightViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.flight_list_view_item, parent, false);
            holder = new FlightViewHolder();

            if (screenUtility.getWidth() > 700.0) {
                holder.flight_state_text_view = (TextView) convertView.findViewById(R.id.flight_list_status_text);
            }

            holder.flight_state_image_view = (ImageView) convertView.findViewById(R.id.avatar_state_image_view);
            holder.flight_airline_id_text_view = (TextView) convertView.findViewById(R.id.flight_list_airline_id_text);
            holder.flight_number_text_view = (TextView) convertView.findViewById(R.id.flight_list_number_text);
            holder.flight_time_text_view = (TextView) convertView.findViewById(R.id.flight_list_time_text);
            holder.flight_date_text_view = (TextView) convertView.findViewById(R.id.flight_list_date_text);
            try {
                holder.flight_takeoff_text_view = (TextView) convertView.findViewById(R.id.flight_list_takeoff_text);
                holder.flight_land_text_view = (TextView) convertView.findViewById(R.id.flight_list_land_text);
            }catch (Exception e) {
                //nothing
            }
            convertView.setTag(holder);
        } else {
            holder = (FlightViewHolder) convertView.getTag();
        }

        holder.flight_number_text_view.setText(getItem(position).getFlight_number().toString());
        holder.flight_airline_id_text_view.setText(getItem(position).getAirline().getAirlineId());
        if (holder.flight_takeoff_text_view != null) {
            holder.flight_takeoff_text_view.setText(getItem(position).getDeparture().getAirport().getCity().getId());
            holder.flight_land_text_view.setText(getItem(position).getArrival().getAirport().getCity().getId());
        }

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date departure_date;
        Date arrival_date;
        Departure departure = getItem(position).getDeparture();
        try {
            departure_date = parser.parse(departure.getScheduled_time());
            holder.flight_date_text_view.setText(dateFormat.format(departure_date));
            holder.flight_time_text_view.setText(timeFormat.format(departure_date));
        }catch (Exception e){
            Toast.makeText(getContext(),
                    getContext().getResources().getString(R.string.no_connection_error),
                    Toast.LENGTH_SHORT).show();
        }
        String status = getItem(position).getStatus();
        if (status.equals("S")){
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_programmed);
            if (screenUtility.getWidth() > 700.0) {
                holder.flight_state_text_view.setText(getContext().getResources().getString(R.string.status_programmed));
            }
        }else if (status.equals("L")){
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_landed);
            if (screenUtility.getWidth() > 700.0) {
                holder.flight_state_text_view.setText(getContext().getResources().getString(R.string.status_landed));
            }
        }else if (status.equals("A")){
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_active);
            if (screenUtility.getWidth() > 700.0) {
                holder.flight_state_text_view.setText(getContext().getResources().getString(R.string.status_active));
            }
        }else if (status.equals("C")){
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_canceled);
            if (screenUtility.getWidth() > 700.0) {
                holder.flight_state_text_view.setText(getContext().getResources().getString(R.string.status_canceled));
            }
        }else if (status.equals("R")){
            holder.flight_state_image_view.setImageResource(R.drawable.ic_avatar_delayed);
            if (screenUtility.getWidth() > 700.0) {
                holder.flight_state_text_view.setText(getContext().getResources().getString(R.string.status_delayed));
            }
        }

        return convertView;
    }
}
