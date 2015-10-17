package com.boilermakeproject.hometown;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by noahrinehart on 10/17/15.
 */
public class ContactFragment extends Fragment{

    private SQLController dbcon;
    private ListView listView;

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



        Cursor cursor = dbcon.fetch();
        String[] from = new String[] {
                DBHelper._ID,
                DBHelper.NAME,
                DBHelper.HOMETOWN_NAME,
                /*DBHelper.HOMETOWN_LAT,
                DBHelper.HOMETOWN_LON,
                DBHelper.IMAGE,
                DBHelper.PHONE_NUM,
                DBHelper.NOTE*/
        };

        int[] to = new int[]{R.id.id, R.id.name, R.id.hometown};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this.getContext(), R.layout.cantact_list_item, cursor, from, to);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView id_tv = (TextView) view.findViewById(R.id.id);
                String id_string = id_tv.getText().toString();
                Toast.makeText(getContext(), "Click ListItem Number " + position + "_id" + id_string, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}


