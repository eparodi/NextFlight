package com.example.martin.nextflight.status_tab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.martin.nextflight.R;
import com.example.martin.nextflight.elements.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFlightFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFlightFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFlightFragment extends Fragment implements OnMapReadyCallback {

    final static private String JSON_STATUS = "JSON_STATUS";
    final private static String STATUS = "status";
    private Status status;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private OnFragmentInteractionListener mListener;

    public MapFlightFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param json_status
     * @return A new instance of fragment MapFlightFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFlightFragment newInstance(String json_status) {
        MapFlightFragment fragment = new MapFlightFragment();
        Bundle args = new Bundle();
        args.putString(JSON_STATUS, json_status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                String json = getArguments().getString(JSON_STATUS);
                JSONObject obj = new JSONObject(json);
                Gson gson = new Gson();

                String jsonFragment = obj.getString(STATUS);

                status = gson.fromJson(jsonFragment, com.example.martin.nextflight.elements.Status.class);
            }catch(Exception e){
                status = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_flight, container, false);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        MapView mapView = (MapView) view.findViewById(R.id.flight_map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.867, 151.206);

        try {
            googleMap.setMyLocationEnabled(true);
        }catch(SecurityException e){
            // TODO: Handle no permissions.
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        googleMap.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
