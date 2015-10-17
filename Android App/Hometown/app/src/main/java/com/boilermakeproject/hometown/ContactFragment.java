package com.boilermakeproject.hometown;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;

/**
 * Created by noahrinehart on 10/17/15.
 */
public class ContactFragment extends Fragment{

    private SQLController dbcon;
    private ListView listView;
    FloatingActionButton fab;
    SimpleCursorAdapter adapter;

    @Override
    public void onResume(){
        super.onResume();
        Cursor cursor = dbcon.fetch();
        String[] from = new String[] {
                DBHelper._ID,
                DBHelper.NAME,
                DBHelper.HOMETOWN_NAME,
                DBHelper.HOMETOWN_LAT,
                DBHelper.HOMETOWN_LON,
                DBHelper.IMAGE,
                DBHelper.PHONE_NUM,
                DBHelper.NOTE
        };

        int[] to = new int[]{R.id.id_holder, R.id.name, R.id.hometown, R.id.lat_holder, R.id.long_holder, R.id.image_holder, R.id.num_holder, R.id.note_holder};
        adapter = new SimpleCursorAdapter(this.getContext(), R.layout.cantact_list_item, cursor, from, to);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        dbcon = new SQLController(this.getContext());
        try{
            dbcon.open();

        }catch (SQLException e){
            e.printStackTrace();
        }
        listView = (ListView) view.findViewById(R.id.contact_list);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddContact.class);
                startActivity(i);
            }
        });

        //String name, String hometown_lat, String hometown_lon, String hometown_name, byte[] image, String phone_num, String note


        Cursor cursor = dbcon.fetch();
        String[] from = new String[] {
                DBHelper._ID,
                DBHelper.NAME,
                DBHelper.HOMETOWN_NAME,
                DBHelper.HOMETOWN_LAT,
                DBHelper.HOMETOWN_LON,
                DBHelper.IMAGE,
                DBHelper.PHONE_NUM,
                DBHelper.NOTE
        };

        int[] to = new int[]{R.id.id_holder, R.id.name, R.id.hometown, R.id.lat_holder, R.id.long_holder, R.id.image_holder, R.id.num_holder, R.id.note_holder};
        adapter = new SimpleCursorAdapter(this.getContext(), R.layout.cantact_list_item, cursor, from, to);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView id_tv = (TextView) view.findViewById(R.id.id_holder);
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView home = (TextView) view.findViewById(R.id.hometown);
                TextView lat = (TextView) view.findViewById(R.id.lat_holder);
                TextView lon = (TextView) view.findViewById(R.id.long_holder);
                ImageView image = (ImageView) view.findViewById(R.id.image_holder);
                TextView num = (TextView) view.findViewById(R.id.num_holder);
                TextView note = (TextView)view.findViewById(R.id.note_holder);

                String id_string = id_tv.getText().toString();
                String name_string = name.getText().toString();
                String home_string = home.getText().toString();
                String lat_string = lat.getText().toString();
                String lon_string = lon.getText().toString();

//                Bitmap bm = image.getDrawingCache();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] bytes = stream.toByteArray();

                String num_string = num.getText().toString();
                String note_string = note.getText().toString();

                Intent modify_intent = new Intent(getContext(), AddContact.class);
                modify_intent.putExtra("id", id_string);
                modify_intent.putExtra("name", name_string);
                modify_intent.putExtra("home", home_string);
                modify_intent.putExtra("lat", lat_string);
                modify_intent.putExtra("lon", lon_string);
                modify_intent.putExtra("num", num_string);
                modify_intent.putExtra("note", note_string);
                startActivity(modify_intent);



            }
        });

        return view;
    }
}


