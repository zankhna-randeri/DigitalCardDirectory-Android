package com.avengers.businesscardapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataControllerBusinessCard {
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final String EMAIL_ID = "EmailId";
    public static final String PASSWORD = "Password";
    public static final String TABLE_NAME = "Customer_Info";
    public static final String DATABASE_NAME = "BusinessCard.db";
    public static final int DATABASE_VERSION = 6;
    public static final String TABLE_CREATE = "create table Customer_Info (FirstName text not null, " +
            " LastName text not null, EmailId text not null, Password text not null)";

    DataBaseHelper dbHelper;
    Context context;
    SQLiteDatabase db;

    public DataControllerBusinessCard(Context context) {
        this.context = context;
        dbHelper = new DataBaseHelper(context);
    }

    public DataControllerBusinessCard open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //method call to insert User records to the db
    public long insert(String firstName, String lastName, String emailId, String password) {
        ContentValues content = new ContentValues();
        content.put(FIRST_NAME, firstName);
        content.put(LAST_NAME, lastName);
        content.put(EMAIL_ID, emailId);
        content.put(PASSWORD, password);
        return db.insertOrThrow(TABLE_NAME, null, content);
    }

    //method call for the sign up validation if the user already exists
    public String retrieveCustomerInfo(String emailId) {
        String existingEmailId = "";
        String query = "SELECT * FROM Customer_Info WHERE EmailId='" + emailId + "'";
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            existingEmailId = cursor.getString(2);
        }
        cursor.close();
        return existingEmailId;
    }

    //method call for the login validation if the email and password exists
    public Cursor validateLoginCredentials(String emailId) {
        String query = "SELECT * FROM Customer_Info WHERE EmailId='" + emailId + "'";
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            try {
                db.execSQL(TABLE_CREATE);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS Customer_Info");
            onCreate(db);
        }

    }

}
