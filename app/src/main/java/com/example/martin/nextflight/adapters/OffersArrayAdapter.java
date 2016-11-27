package com.example.martin.nextflight.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martin.nextflight.R;
import com.example.martin.nextflight.elements.Deal;
import com.example.martin.nextflight.holders.OffersViewHolder;
import com.example.martin.nextflight.holders.ReviewViewHolder;

import java.util.ArrayList;

/**
 * Created by Martin on 23/11/2016.
 */

public class OffersArrayAdapter extends ArrayAdapter<Deal> {

    String currency;

    public OffersArrayAdapter(Activity context, ArrayList<Deal> deals_list, String currency) {
        super(context, R.layout.offers_result_list_item, deals_list);
        this.currency = currency;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OffersViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.offers_result_list_item, parent, false);
            holder = new OffersViewHolder();
            holder.offers_destination_text_view = (TextView) convertView.findViewById(R.id.offers_result_to_item);
            holder.offers_price_text_view = (TextView) convertView.findViewById(R.id.offers_result_price_item);
            //holder.offers_destination_image_view = (ImageView) convertView.findViewById(R.id.offers_result_image_item);
            convertView.setTag(holder);
        } else {
            holder = (OffersViewHolder) convertView.getTag();
        }

        Double price = getItem(position).getPrice();
        if (currency != null) {
            if (currency.equals("Pesos"))
                price *= 15.49;
            else if (currency.equals("Reales"))
                price *= 3.38;
        }

        holder.offers_destination_text_view.setText("A: " + getItem(position).getCity().getName());
        holder.offers_price_text_view.setText("Precio: " + price);

        //holder.offers_destination_image_view.setImageBitmap(getItem(position).getImage());
        return convertView;
    }

}
