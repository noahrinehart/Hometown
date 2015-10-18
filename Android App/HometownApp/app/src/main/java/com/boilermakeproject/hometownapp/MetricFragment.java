package com.boilermakeproject.hometownapp;


import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MetricFragment extends Fragment implements LocationListener {


    ArrayList<Contact> contacts;
    Contact[] contactArray;
    GoogleApiClient mClient;
    LocationManager lm;
    Location l;
    double cur_lat;
    String provider;
    double cur_lon;
    TextView closeContact;
    TextView furthestContact;
    TextView localsContact;
    TextView percContact;
    TextView avgContact;
    TextView avgDistContact;
    ArrayList<Contact> contactArrayList;
    private SQLController dbcon;

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

    public MetricFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_metric, container, false);

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


        dbcon = new SQLController(getContext());

        try{
            dbcon.open();

        }catch (SQLException e){
            e.printStackTrace();
        }
        //dbcon.insert("Noah Rinehart", "Purdue", "40.4240", "-86.9290");
        contactArrayList = new ArrayList<Contact>();
        Cursor cursor = dbcon.fetch();
        while (cursor.moveToNext()) {
            String _name = cursor.getString(1);
            String _home = cursor.getString(2);
            String _lat = cursor.getString(3);
            String _lon = cursor.getString(4);

            if(_name != null)
                contactArrayList.add(new Contact(_name, _home, Double.parseDouble(_lat), Double.parseDouble(_lon)));
        }
        if (contactArrayList != null)
            Log.d("num_contact", Integer.toString(contactArrayList.size()));



        Contact me = new Contact("Me", "Location", cur_lat, cur_lon);
        contactArray = new Contact[contactArrayList.size()];
        contactArray = contactArrayList.toArray(contactArray);

        if (contactArray.length > 1) {


            //closest, furthest, number of locals, percent within 50 miles,avg distance, avg distance between

            localsContact = (TextView) view.findViewById(R.id.locals_contact);
            percContact = (TextView) view.findViewById(R.id.perc_contact);
            avgContact = (TextView) view.findViewById(R.id.avg_contact);
            avgDistContact = (TextView) view.findViewById(R.id.avg_dist);



            int local = locals(me, contactArray);
            double perc = percLocal(local, contactArray);
            double avg = avgDistanceFromMe(me, contactArray);
            double avgdist = avgDistanceBetween(contactArray);

            DecimalFormat df = new DecimalFormat("#.##");
            perc = Double.valueOf(df.format(perc));
            avg = Double.valueOf(df.format(avg));
            avgdist = Double.valueOf(df.format(avgdist));

            localsContact.setText(Integer.toString(local) + " people within 50 miles");
            percContact.setText(Double.toString(perc) + "%");
            avgContact.setText(Double.toString(avg) + " miles");
            avgDistContact.setText(Double.toString(avgdist) + " miles");

        }

        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {

                contactArrayList = new ArrayList<Contact>();
                Cursor cursor = dbcon.fetch();
                while (cursor.moveToNext()) {
                    String _name = cursor.getString(1);
                    String _home = cursor.getString(2);
                    String _lat = cursor.getString(3);
                    String _lon = cursor.getString(4);

                    if(_name != null)
                        contactArrayList.add(new Contact(_name, _home, Double.parseDouble(_lat), Double.parseDouble(_lon)));
                }
                if (contactArrayList != null)
                    Log.d("num_contact", Integer.toString(contactArrayList.size()));



                Contact me = new Contact("Me", "Location", cur_lat, cur_lon);
                contactArray = new Contact[contactArrayList.size()];
                contactArray = contactArrayList.toArray(contactArray);

                if (contactArray.length > 1) {


                    //closest, furthest, number of locals, percent within 50 miles,avg distance, avg distance between

                    localsContact = (TextView) view.findViewById(R.id.locals_contact);
                    percContact = (TextView) view.findViewById(R.id.perc_contact);
                    avgContact = (TextView) view.findViewById(R.id.avg_contact);
                    avgDistContact = (TextView) view.findViewById(R.id.avg_dist);



                    int local = locals(me, contactArray);
                    double perc = percLocal(local, contactArray);
                    double avg = avgDistanceFromMe(me, contactArray);
                    double avgdist = avgDistanceBetween(contactArray);

                    DecimalFormat df = new DecimalFormat("#.##");
                    perc = Double.valueOf(df.format(perc));
                    avg = Double.valueOf(df.format(avg));
                    avgdist = Double.valueOf(df.format(avgdist));

                    localsContact.setText(Integer.toString(local) + " people within 50 miles");
                    percContact.setText(Double.toString(perc) + "%");
                    avgContact.setText(Double.toString(avg) + " miles");
                    avgDistContact.setText(Double.toString(avgdist) + " miles");

                }
            }
        });

        return view;
    }
    public double rad(double degrees) {
        return degrees * Math.PI / 180;
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {

        double R = 3961;
        double dlon = rad(lon2 - lon1);
        double dlat = rad(lat2 - lat1);
        double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(rad(lat1)) * Math.cos(rad(lat2)) * Math.pow(Math.sin
                (dlon/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 -a) );
        return R * c; //(where R is the radius of the Earth)
    }


    public Contact closest(Contact me, Contact[] contacts) {

        Contact closest = contacts[0];
        double shortest = getDistance(me.getLatitude(), me.getLongitude(), contacts[0].getLatitude(), contacts[0]
                .getLongitude());
        for (Contact c : contacts) {
            if ((getDistance(me.getLatitude(), me.getLongitude(), c.getLatitude(), c.getLongitude
                    ())) < shortest)
                closest = c;

        }
        return closest;
    }
    public Contact furthest(Contact me, Contact[] contacts){
        Contact furthest = contacts[0];
        double mostFar = getDistance(me.getLatitude(), me.getLongitude(), contacts[0].getLatitude(), contacts[0].getLatitude());
        for (Contact c : contacts){
            if ((getDistance(me.getLatitude(), me.getLongitude(), c.getLatitude(), c.getLongitude())) > mostFar)
                furthest = c;
        }
        return furthest;
    }


    public int locals(Contact me, Contact[] contacts) {
        int locals = 0;
        for (Contact c : contacts) {
            if (getDistance(me.getLatitude(), me.getLongitude(), c.getLatitude(), c.getLongitude()) < 50)
                locals++;
        }
        return locals;
    }

    public double percLocal(int locals, Contact[] contacts) {
        return locals * 100.0 / contacts.length;
    }

    public double avgDistanceFromMe(Contact me, Contact[] contacts) {
        double tot = 0;
        for (Contact c : contacts) {
            tot += getDistance(me.getLatitude(), me.getLongitude(), c.getLatitude(), c.getLongitude());
        }
        return tot / contacts.length;
    }


    public double avgDistanceBetween(Contact[] contacts) {
        double tot = 0;
        int k = 1;
        for (Contact c : contacts) {
            for (int i = k; i < contacts.length; i++) {
                tot += getDistance(c.getLatitude(), c.getLongitude(), contacts[i].getLatitude(), contacts[i]
                        .getLongitude());
            }
            k++;
        }
        double combs = (contacts.length * (contacts.length - 1)) / 2;
        return tot / combs;
    }




}







