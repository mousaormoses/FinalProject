package com.example.finalproject.NasaImage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseNasaImage extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NasaImage";
    public static final String TABLE_NAME = "ImagesTable";
    public final static int VERSION_NUM = 2;

    //columns
    public final static String COL_ID = "_id";
    public static final String COL_DATE = "DATE";
    public static final String COL_REGURL = "RegURL";
    public static final String COL_HDURL = "HdURL";
    public static final String COL_MESG = "messges";


    public DatabaseNasaImage(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DATE TEXT, RegURL TEXT, HdURL TEXT, messges TEXT)");
        // add or remove columns
    }

    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {   //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    public boolean inserData(String date, String regUrl, String hdUrl,String messages) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_REGURL, regUrl);
        contentValues.put(COL_HDURL, hdUrl);
        contentValues.put(COL_MESG, messages);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    //view data
    public Cursor viewAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }


}
