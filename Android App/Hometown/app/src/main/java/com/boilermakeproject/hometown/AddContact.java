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
import com.google.android.gms.location.places.Place;
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



public class AddContact extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private SQLController dbcon;
    EditText nameText;
    EditText homeText;
    EditText numText;
    EditText noteText;
    Button saveButton;
    Button deleteButton;
    Button gotoauto;
    AutoCompleteTextView autoPlace;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyCvtbH-lm7xf1jwcCiUblX3tZhJqI-ZL38";

    private static final String LOG_TAG = "AddContact";
    protected GoogleApiClient mGoogleApiClient;
    private TextView mPlaceDetailsText;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private TextView mPlaceDetailsAttribution;
    private TextView mNameTextView;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private PlaceArrayAdapter mPlaceArrayAdapter;

    private TextView desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mGoogleApiClient = new GoogleApiClient.Builder(AddContact.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();


        nameText = (EditText) findViewById(R.id.contact_name);
        homeText = (EditText)findViewById(R.id.hometown_add);
        numText = (EditText)findViewById(R.id.contact_phone);
        noteText = (EditText)findViewById(R.id.contact_note);

        saveButton = (Button)findViewById(R.id.save_contact);
        deleteButton = (Button)findViewById(R.id.delete_contact);
        autoPlace = (AutoCompleteTextView) findViewById(R.id.places_auto);
        autoPlace.setThreshold(5);
        desc = (TextView)findViewById(R.id.desc);
        autoPlace.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,BOUNDS_GREATER_SYDNEY, null);
        autoPlace.setAdapter(mPlaceArrayAdapter);
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
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            mNameTextView.setText(Html.fromHtml(place.getName() + ""));

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

}
