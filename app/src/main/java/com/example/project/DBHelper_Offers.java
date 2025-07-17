package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper_Offers extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RealEstateDB_Offers.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_OFFERS = "SpecialOffers";
    private static final String COL_ID = "id";
    private static final String COL_PROPERTY_ID = "propertyId";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_START_DATE = "startDate";
    private static final String COL_END_DATE = "endDate";

    public DBHelper_Offers(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createOffersTable = "CREATE TABLE " + TABLE_OFFERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PROPERTY_ID + " INTEGER NOT NULL, "
                + COL_TITLE + " TEXT NOT NULL, "
                + COL_DESCRIPTION + " TEXT, "
                + COL_START_DATE + " TEXT NOT NULL, "
                + COL_END_DATE + " TEXT NOT NULL"
                + ")";
        db.execSQL(createOffersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFERS);
        onCreate(db);
    }

    public boolean addSpecialOffer(int propertyId, String title, String description, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_PROPERTY_ID, propertyId);
        values.put(COL_TITLE, title);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_START_DATE, startDate);
        values.put(COL_END_DATE, endDate);

        long result = db.insert(TABLE_OFFERS, null, values);
        db.close();
        return result != -1;      }

    public Cursor getAllSpecialOffers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_OFFERS + " ORDER BY " + COL_START_DATE + " DESC", null);
    }
}