package com.boilermakeproject.hometown;

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

    public SQLController open() throws SQLException{
        dbHelper = new DBHelper(ourcontext);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public void insert(String name, String hometown_lat, String hometown_lon, String hometown_name, byte[] image, String phone_num, String note){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, name);
        contentValue.put(DBHelper.HOMETOWN_LAT, hometown_lat);
        contentValue.put(DBHelper.HOMETOWN_LON, hometown_lon);
        contentValue.put(DBHelper.HOMETOWN_NAME, hometown_name);
        contentValue.put(DBHelper.PHONE_NUM, phone_num);
        contentValue.put(DBHelper.IMAGE, image);
        contentValue.put(DBHelper.NOTE, note);
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch(){
        String[] columns = new String[] {
                DBHelper._ID,
                DBHelper.NAME,
                DBHelper.HOMETOWN_LAT,
                DBHelper.HOMETOWN_LON,
                DBHelper.HOMETOWN_NAME,
                DBHelper.IMAGE,
                DBHelper.PHONE_NUM,
                DBHelper.NOTE
        };
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;

    }

    public int update(long _id, String name, String hometown_lat, String hometown_lon, String hometown_name, byte[] image, String phone_num, String note){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NAME, name);
        contentValue.put(DBHelper.HOMETOWN_LAT, hometown_lat);
        contentValue.put(DBHelper.HOMETOWN_LON, hometown_lon);
        contentValue.put(DBHelper.HOMETOWN_NAME, hometown_name);
        contentValue.put(DBHelper.PHONE_NUM, phone_num);
        contentValue.put(DBHelper.IMAGE, image);
        contentValue.put(DBHelper.NOTE, note);
        int i = database.update(DBHelper.TABLE_NAME, contentValue, DBHelper._ID + " = " + _id, null);
        return i;
    }
    public void delete(long _id){
        database.delete(DBHelper.TABLE_NAME, DBHelper._ID + "=" + _id, null);
    }

}
