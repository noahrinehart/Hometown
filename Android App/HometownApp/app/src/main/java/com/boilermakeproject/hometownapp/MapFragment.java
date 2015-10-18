package com.boilermakeproject.hometownapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by noahrinehart on 10/17/15.
 */
public class MapFragment extends Fragment implements LocationListener {

    private FloatingActionButton fab;
    TextView textView;
    MapView mapView;
    GoogleMap map;
    String name;
    String hometown_name;
    String lat;
    String lon;
    private SQLController dbcon;
    GoogleApiClient mClient;
    LocationManager lm;
    String provider;
    Location l;
    double cur_lat;
    double cur_lon;
    ArrayList<Contact> contactArrayList;


    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }



    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onPause(){
        super.onPause();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);


        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                }).build();




        textView = (TextView) view.findViewById(R.id.textView);
        dbcon = new SQLController(getContext());

        lm=(LocationManager)this.getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria c=new Criteria();
        provider=lm.getBestProvider(c, false);
        l=lm.getLastKnownLocation(provider);
        if(l!=null)
        {
            //get latitude and longitude of the location
            cur_lon=l.getLongitude();
            cur_lat=l.getLatitude();
            Log.d("loc", Double.toString(cur_lon));
        }

        contactArrayList = new ArrayList<Contact>();



        try{
            dbcon.open();

        }catch (SQLException e){
            e.printStackTrace();
        }

        //dbcon.insert("Noah Rinehart", "Purdue", "40.4240", "-86.9290");

        int i = 0;
        Cursor cursor = dbcon.fetch();
        while (cursor.moveToNext()) {
            String _name = cursor.getString(1);
            String _home = cursor.getString(2);
            String _lat = cursor.getString(3);
            String _lon = cursor.getString(4);
            contactArrayList.add(new Contact(_name, _home, Double.parseDouble(_lat), Double.parseDouble(_lon)));
        }
        Log.d("num", Integer.toString(contactArrayList.size()));



        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), AddPersonActivity.class);
                startActivityForResult(i, 1);
            }
        });
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());



        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(cur_lat, cur_lon), 10);
        map.animateCamera(cameraUpdate);




        drawPins();

        return view;
    }



    @Override
    public void onLocationChanged(Location arg0)
    {
        cur_lon=l.getLongitude();
        cur_lat=l.getLatitude();
        Log.d("loc", Double.toString(cur_lon));
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                name=data.getStringExtra("name");
                hometown_name=data.getStringExtra("hometown_name");
                lat = data.getStringExtra("hometown_lat");
                lon = data.getStringExtra("hometown_lon");
                dbcon.insert(name, hometown_name, lat, lon);
                drawPins();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), 10);
                map.animateCamera(cameraUpdate);

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }//onAc







    public void drawPins(){
        Cursor cursor = dbcon.fetch();
        while (cursor.moveToNext()){
            String _name = cursor.getString(1);
            String _home = cursor.getString(2);
            String _lat = cursor.getString(3);
            String _lon = cursor.getString(4);
            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(_lat), Double.parseDouble(_lon))).title(_name + "\n" + _home));
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            PolylineOptions rectOptions = new PolylineOptions()
                    .add(new LatLng(cur_lat, cur_lon))
                    .add(new LatLng(Double.parseDouble(_lat), Double.parseDouble(_lon))).color(color);
            Polyline polyline = map.addPolyline(rectOptions);
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        drawPins();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        drawPins();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        drawPins();
    }



}
