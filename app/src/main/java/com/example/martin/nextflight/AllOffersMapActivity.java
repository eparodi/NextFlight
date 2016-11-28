package com.example.martin.nextflight;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.martin.nextflight.elements.Airport;
import com.example.martin.nextflight.elements.City;
import com.example.martin.nextflight.elements.Deal;
import com.example.martin.nextflight.elements.Flight;
import com.example.martin.nextflight.elements.oneWayFlight.OneWayFlight;
import com.example.martin.nextflight.elements.oneWayFlight.Segment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class AllOffersMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Deal> flights;
    private City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_offers_map);
        Bundle bundle = getIntent().getExtras();

        flights = ((ArrayList<Deal>) bundle.getSerializable("FlightList"));
        city = (City) bundle.getSerializable("DepartureCity");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        City departure_city = city;
        addMarker(departure_city);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(city.getLatitude(),city.getLongitude()), 4));
        for ( Deal d : flights ){
            City arrival_city = d.getCity();
            addMarker(arrival_city);
            addRoute(arrival_city,departure_city, d.getPrice());
        }
    }

    private void addMarker(City a){
        LatLng latLng = new LatLng(a.getLatitude(),a.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(a.getName()));
    }

    private void addRoute(City arr, City dep, double price){
        LatLng depLatLng = new LatLng(dep.getLatitude(),dep.getLongitude());
        LatLng arrLatLng = new LatLng(arr.getLatitude(),arr.getLongitude());

        mMap.addPolyline(new PolylineOptions().geodesic(true).width(2).color(generateColor(price))
                .add(depLatLng)  // Departure
                .add(arrLatLng)  // Arrival
        );
    }

    // TODO: perform this algorithm.
    private int generateColor(double price){
        int red;
        int green;
        if ( price < 300){
            green = 255;
            red = 0;
        }else if ( price > 500 ){
            red = 255;
            green = 0;
        }else{
            red = (int)(255 * (price - 200)) / 1300;
            green = (int)(-255 * (price - 1500)) / 1300;
        }
        return Color.argb(200,red,green,100);
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
