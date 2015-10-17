package com.boilermakeproject.hometown;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class AddContact extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private SQLController dbcon;
    EditText nameText;
    EditText homeText;
    EditText numText;
    EditText noteText;
    Button saveButton;
    Button deleteButton;
    Button gotoauto;
    AutoCompleteTextView autoPlace;
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyCvtbH-lm7xf1jwcCiUblX3tZhJqI-ZL38";


    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private TextView mPlaceDetailsText;
    private TextView mPlaceDetailsAttribution;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();


        setContentView(R.layout.activity_add_contact);


        nameText = (EditText) findViewById(R.id.contact_name);
        homeText = (EditText)findViewById(R.id.hometown_add);
        numText = (EditText)findViewById(R.id.contact_phone);
        noteText = (EditText)findViewById(R.id.contact_note);

        saveButton = (Button)findViewById(R.id.save_contact);
        deleteButton = (Button)findViewById(R.id.delete_contact);
        autoPlace = (AutoCompleteTextView) findViewById(R.id.places_auto);
        autoPlace.setThreshold(5);
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        autoPlace.setAdapter(mAdapter);

        dbcon = new SQLController(getApplicationContext());
        try{
            dbcon.open();

        }catch (SQLException e){
            e.printStackTrace();
        }
        if (this.getIntent().getExtras() != null) {
            Intent intent = getIntent();
            final String id = intent.getStringExtra("id");
            final String name = intent.getStringExtra("name");
            final String home = intent.getStringExtra("home");
            final String lat = intent.getStringExtra("lat");
            final String lon = intent.getStringExtra("long");
            final String num = intent.getStringExtra("num");
            final String note = intent.getStringExtra("note");
            nameText.setText(name);
            homeText.setText(home);
            numText.setText(num);
            noteText.setText(note);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbcon.update(Long.parseLong(id), nameText.getText().toString(), "0", "0", homeText.getText().toString(), null, numText.getText().toString(), noteText.getText().toString());
                    AddContact.this.finish();
                }
            });
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbcon.delete(Long.parseLong(id));
                    //insert dialog
                    //Toast toast = new Toast.makeText(getApplicationContext(), "deleted " + name, Toast.LENGTH_SHORT).show();
                    AddContact.this.finish();

                }
            });


        }
        if (this.getIntent().getExtras() == null){
            nameText.setHint("Name");
            homeText.setHint("Home");
            numText.setHint("Phone Number");
            noteText.setHint("Notes");
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbcon.insert(nameText.getText().toString(), "0", "0", homeText.getText().toString(), null, numText.getText().toString(), noteText.getText().toString());
                AddContact.this.finish();
            }
        });




    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);


            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
        }
    };


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final com.google.android.gms.location.places.Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }


            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {



    }



}
