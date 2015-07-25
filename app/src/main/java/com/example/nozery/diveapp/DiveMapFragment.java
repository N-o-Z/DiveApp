package com.example.nozery.diveapp;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiveMapFragment.OnMapInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiveMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiveMapFragment extends MapFragment
		implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "divePois";

    private ArrayList<DiveBasePoi> mDivePois ;

    private OnMapInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param divePois List of POI to present on the map.
     * @return A new instance of fragment DiveMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiveMapFragment newInstance(ArrayList<DiveBasePoi> divePois) {
        DiveMapFragment fragment = new DiveMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, divePois);
        fragment.setArguments(args);

        return fragment;
    }

    public DiveMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDivePois = (ArrayList<DiveBasePoi>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = super.onCreateView(inflater, container, savedInstanceState);

        getMapAsync(this);

        // Set title bar
         ((MainActivity) getActivity())
                 .setActionBarTitle("Map");

        setHasOptionsMenu(true);

        return (view) ;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMapInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMapInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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

        LatLng coordinate = new LatLng(29.513514, 34.926509) ; //TODO: get current location
        googleMap.setMyLocationEnabled(true);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(coordinate, 12) ;
        googleMap.animateCamera(location);

        PutDivePoiOnMap(mDivePois);

    }

    private void ShowDivePOI(LatLng CurLocation)
    {
            LatLng p1 = new LatLng(3, 5) ;
            LatLng p2 = new LatLng(3, 5) ;

            computeDistanceBetween(p1, p2);
    }

    private void PutDivePoiOnMap(ArrayList<DiveBasePoi> divePoi)
    {
        GoogleMap map = getMap();

        Double lng = 0.0 ;
        Double lat = 0.0 ;

        String name ;

        Iterator<DiveBasePoi> poiIterator = divePoi.iterator();
        while (poiIterator.hasNext()) {

            DiveBasePoi poi =  poiIterator.next();

            name = poi.getValue(ParseDbHelper.DiveSitesPoiEntry.COLUMN_NAME_POI_NAME);
            lat = Double.parseDouble(
                    poi.getValue(ParseDbHelper.DiveSitesPoiEntry.COLUMN_NAME_LATITUDE));
            lng = Double.parseDouble(
                    poi.getValue(ParseDbHelper.DiveSitesPoiEntry.COLUMN_NAME_LONGITUDE));

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(name)
            );
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnMapInteractionListener {
        // TODO: Update argument type and name
        void onMapInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_activity_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                // do s.th.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
