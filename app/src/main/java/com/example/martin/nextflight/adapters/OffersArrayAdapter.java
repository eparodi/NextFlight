package com.example.martin.nextflight.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.nextflight.OffersResultActivity;
import com.example.martin.nextflight.R;
import com.example.martin.nextflight.elements.Deal;
import com.example.martin.nextflight.elements.flickr.FlickrObject;
import com.example.martin.nextflight.elements.flickr.Photo;
import com.example.martin.nextflight.holders.OffersViewHolder;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Martin on 23/11/2016.
 */

public class OffersArrayAdapter extends ArrayAdapter<Deal> {

    String currency;
    //public HashMap<Deal, View> holderHashMap = new HashMap<>();

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
            holder.offers_destination_image_view = (ImageView) convertView.findViewById(R.id.offers_result_image_item);
            convertView.setTag(holder);
        } else {
            holder = (OffersViewHolder) convertView.getTag();
        }
        Deal deal = getItem(position);
        //holderHashMap.put(this.getItem(position),convertView);
        Double price = deal.getPrice();
        if (currency != null) {
            if (currency.equals("Pesos"))
                price *= 15.49;
            else if (currency.equals("Reales"))
                price *= 3.38;
        }
        price = Math.floor(price * 100) / 100;

        holder.offers_destination_text_view.setText("" + deal.getCity().getName());
        holder.offers_price_text_view.setText("Precio: " + price);
        if (deal.getImage() == null && !deal.loadImage){
            deal.loadImage = true;
            new HttpGetPhotos(this.getItem(position),holder).execute();
        }
        holder.offers_destination_image_view.setImageBitmap(deal.getImage());
        return convertView;
    }


    private class HttpGetPhotos extends AsyncTask<Void, Void, String> {

        private Deal deal;
        private OffersViewHolder holder;

        public HttpGetPhotos(Deal deal, OffersViewHolder holder) {
            this.deal = deal;
            this.holder = holder;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            String method = "&method=flickr.photos.search";
            String api_key = "&api_key=386aa6fb081b08ba87dd7efaf75f04f0";
            String per_page = "&per_page=1";
            String text = "&text=" + "";
            String format = "&format=json";

            try {
                text += URLEncoder.encode(deal.getCity().getName(), "UTF-8");
            } catch(Exception exception) {
                Toast.makeText(getContext(),
                        getContext().getResources().getString(R.string.no_connection_error),
                        Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
                return getContext().getResources().getString(R.string.error);
            }


            try {
                URL url = new URL("https://api.flickr.com/services/rest/?" + method + api_key + per_page + text + format);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception exception) {
                Toast.makeText(getContext(),
                        getContext().getResources().getString(R.string.no_connection_error),
                        Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
                return getContext().getResources().getString(R.string.error);
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                //Log.v("STRING",result);

                Gson gson = new Gson();
                Type listType = new TypeToken<FlickrObject>() {
                }.getType();

                String jsonFragment = obj.getString("photos");
                FlickrObject flickrObject = gson.fromJson(jsonFragment, listType);

                // Esto es solo para probar si funciona el LoadImage, usando la imagen del ejemplo.
                //new LoadImage(deal).execute("http://itba.edu.ar/sites/default/themes/itba/assets/images/back.jpg");

                // Este es el pedido que hay que hacer para ver una imagen en flickr, usando los datos recibidos de la consulta.
                Photo photo = flickrObject.getPhoto().get(0);

                new LoadImage(deal,holder).execute("http://farm" + photo.getFarm() + ".static.flickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + "_z.jpg");
                //new LoadImage(deal).execute("https://www.flickr.com/photos/" + flickrObject.getPhoto().get(0).getOwner() + "/" + flickrObject.getPhoto().get(0).getId() + "/");

            } catch (Exception e) {
                Toast.makeText(getContext(),
                        getContext().getResources().getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT).show();
            }

        }

        private String readStream(InputStream inputStream) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int i = inputStream.read();
                int count = 0;
                while (i != -1) {
                    if (count >= 14)
                        outputStream.write(i);
                    i = inputStream.read();
                    count ++;
                }
                String str = outputStream.toString();
                if (str != null && str.length() > 0 && str.charAt(str.length()-1)=='x') {
                    str = str.substring(0, str.length()-1);
                }
                return str;
            } catch (IOException e) {
                return "";
            }
        }
    }

    public class LoadImage extends AsyncTask<String, Void, Bitmap> {

        private Deal deal;
        private OffersViewHolder holder;

        public LoadImage(Deal deal,OffersViewHolder holder) {
            this.deal = deal;
            this.holder = holder;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                return BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent(),null,options);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                deal.setImage(Bitmap.createScaledBitmap(result,150,150,false));
                OffersArrayAdapter.this.notifyDataSetChanged();
                //holder.offers_destination_image_view.setImageBitmap(Bitmap.createScaledBitmap(result,150,150,false));
            } else {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }
}
