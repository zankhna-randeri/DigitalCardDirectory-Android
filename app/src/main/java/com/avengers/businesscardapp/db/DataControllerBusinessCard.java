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
    public static final int DATABASE_VERSION = 7;
    public static final String TABLE_CREATE = "create table Customer_Info (FirstName text not null, " +
            " LastName text not null, EmailId text not null, Password text not null)";

    //logic to add business card information to the DB
    public static final String CONTACT_NAME="ContactName";
    public static final String CONTACT_ORGANIZATION="ContactOrganization";
    public static final String CONTACT_EMAIL="ContactEmail";
    public static final String CONTACT_NUMBER="ContactNUmber";
    public static final String USER_EMAIL="UserEmail";
    public static final String FILE_NAME="FileName";
    public static final String TABLE_NAME_CONTACT="Contact_Info";
    public static final String TABLE_CREATE_CONTACT="create table Contact_Info (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "UserEmail text not null, " +
            " CONTACT_NAME text not null, ContactOrganization text not null, CONTACT_EMAIL text not null," +
            " CONTACT_NUMBER text not null, FILE_NAME text not null)";

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

    //Logic to add the contact Info to the DB and retrieve it from the DB
    public long insertContactInfo(String userEmail, String contactName, String contactOrg, String contactEmail,
                                  String contactNumber, String fileName)
    {
        ContentValues content=new ContentValues();
        content.put(USER_EMAIL, userEmail);
        content.put(CONTACT_NAME, contactName);
        content.put(CONTACT_ORGANIZATION, contactOrg);
        content.put(CONTACT_EMAIL, contactEmail);
        content.put(CONTACT_NUMBER, contactNumber);
        content.put(FILE_NAME, fileName);
        return db.insertOrThrow(TABLE_NAME_CONTACT, null, content);
    }

    public Cursor retrieveContactInfo(String userEmail, Integer ID)
    {
        String query = "SELECT * FROM Contact_Info WHERE UserEmail='" +userEmail+"'" + " AND ID=" + ID;
        db = dbHelper.getReadableDatabase();
        Cursor  cursor = db.rawQuery(query,null);

        return cursor;
    }

//END

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
            try {
                db.execSQL(TABLE_CREATE);
                db.execSQL(TABLE_CREATE_CONTACT);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS Customer_Info");
            db.execSQL("DROP TABLE IF EXISTS Contact_Info");
            onCreate(db);
        }

    }

}
