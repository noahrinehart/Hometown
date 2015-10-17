package com.boilermakeproject.hometown;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by noahrinehart on 10/17/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "CONTACTS";

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String HOMETOWN_LAT = "hometown_lat";
    public static final String HOMETOWN_LON = "hometown_long";
    public static final String HOMETOWN_NAME = "hometown_name";
    public static final String IMAGE = "image";
    public static final String PHONE_NUM = "phone_number";
    public static final String NOTE = "note";


    static final String DB_NAME = "HOMETOWN.DB";

    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAME + " TEXT NOT NULL, " +
            HOMETOWN_LAT + " TEXT NOT NULL, " +
            HOMETOWN_LON + " TEXT NOT NULL, " +
            HOMETOWN_NAME + " TEXT NOT NULL, " +
            IMAGE + " BLOB, " +
            PHONE_NUM + " TEXT, " +
            NOTE + " TEXT); ";

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

