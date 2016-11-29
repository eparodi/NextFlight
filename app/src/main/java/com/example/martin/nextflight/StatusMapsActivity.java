package com.example.martin.nextflight;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class StatusMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double departure_airport_lat;
    private double departure_airport_long;
    private String departure_name;

    private double arrival_airport_lat;
    private double arrival_airport_long;
    private String arrival_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_maps);
        // Obtain the SupportMapFragment and get notified when the help_map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Bundle bundle = getIntent().getExtras();

        departure_airport_lat = bundle.getDouble("DEPLAT");
        departure_airport_long = bundle.getDouble("DEPLONG");
        departure_name = bundle.getString("DEPMARK");
        arrival_airport_lat = bundle.getDouble("ARVLAT");
        arrival_airport_long = bundle.getDouble("ARVLONG");
        arrival_name = bundle.getString("ARVMARK");

        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the help_map once available.
     * This callback is triggered when the help_map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng departure_airport =  new LatLng(departure_airport_lat, departure_airport_long);
        LatLng arrival_airport = new LatLng(arrival_airport_lat, arrival_airport_long);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
               departure_airport, 2));

        mMap.addMarker(new MarkerOptions()
                .title(departure_name)
                .position(departure_airport));

        mMap.addMarker(new MarkerOptions()
                .title(arrival_name)
                .position(arrival_airport));

        // Polylines are useful for marking paths and routes on the help_map.
        mMap.addPolyline(new PolylineOptions().geodesic(true)
                .add(departure_airport)  // Departure
                .add(arrival_airport)  // Arrival
        );
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
