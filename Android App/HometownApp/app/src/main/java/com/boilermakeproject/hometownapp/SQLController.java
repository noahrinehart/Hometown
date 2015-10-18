package com.boilermakeproject.hometownapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by noahrinehart on 10/17/15.
 */
public class SQLController {

    private DBHelper dbHelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLController(Context c){
        ourcontext = c;
    }

    public SQLController open() throws SQLException {
        dbHelper = new DBHelper(ourcontext);
        database = dbHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        dbHelper.close();
    }


    public void insert(String name, String hometown_id, String hometown_lat, String hometown_lon){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, name);
        contentValue.put(DBHelper.HOMETOWN_ID, hometown_id);
        contentValue.put(DBHelper.HOMETOWN_LAT, hometown_lat);
        contentValue.put(DBHelper.HOMETOWN_LON, hometown_lon);
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch(){
        String[] columns = new String[]{
                DBHelper._ID,
                DBHelper.NAME,
                DBHelper.HOMETOWN_ID,
                DBHelper.HOMETOWN_LAT,
                DBHelper.HOMETOWN_LON
        };
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null,null,null,null,null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }




    public int update(long _id, String name, String hometown_id, String hometown_lat, String hometown_lon){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, name);
        contentValue.put(DBHelper.HOMETOWN_ID, hometown_id);
        contentValue.put(DBHelper.HOMETOWN_LAT, hometown_lat);
        contentValue.put(DBHelper.HOMETOWN_LON, hometown_lon);
        int i = database.update(DBHelper.TABLE_NAME, contentValue, DBHelper._ID + "=" + _id, null );
        return i;
    }

}
