package com.avengers.businesscardapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.avengers.businesscardapp.dto.Card;

import java.util.ArrayList;
import java.util.List;

public class DataControllerBusinessCard {

    private static final String DATABASE_NAME = "BusinessCard.db";
    private static final int DATABASE_VERSION = 7;

    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String EMAIL_ID = "EmailId";
    private static final String PASSWORD = "Password";
    private static final String TABLE_NAME_CUSTOMER = "Customer_Info";
    private static final String TABLE_CREATE_CUSTOMER =
            "create table Customer_Info (FirstName text not null, LastName text not null, " +
                    "EmailId text not null, Password text not null)";

    //logic to add business card information to the DB
    private static final String TABLE_NAME_CONTACT = "Contact_Info";
    private static final String CONTACT_ID = "ID";
    private static final String CONTACT_NAME = "ContactName";
    private static final String CONTACT_ORGANIZATION = "ContactOrganization";
    private static final String CONTACT_EMAIL = "ContactEmail";
    private static final String CONTACT_NUMBER = "ContactNumber";
    private static final String USER_EMAIL = "UserEmail";
    private static final String FILE_NAME = "FileName";
    private static final String CONTACT_NOTES = "Notes";


    private static final String TABLE_CREATE_CONTACT = "create table Contact_Info " +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "UserEmail text not null, ContactName text not null, " +
            "ContactOrganization text not null, ContactEmail text not null, " +
            "ContactNumber text not null, FileName text not null, Notes text)";

    private DataBaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase db;

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
    public long insertContactInfo(String userEmail, String contactName,
                                  String contactOrg, String contactEmail,
                                  String contactNumber, String fileName, String notes) {
        ContentValues content = new ContentValues();
        content.put(USER_EMAIL, userEmail);
        content.put(CONTACT_NAME, contactName);
        content.put(CONTACT_ORGANIZATION, contactOrg);
        content.put(CONTACT_EMAIL, contactEmail);
        content.put(CONTACT_NUMBER, contactNumber);
        content.put(FILE_NAME, fileName);
        content.put(CONTACT_NOTES, notes);
        return db.insertOrThrow(TABLE_NAME_CONTACT, null, content);
    }

    /**
     * Fetch card detail for current card and for current app user
     *
     * @param ID Card Id
     * @return Card detail of given cardID for current app user
     */
    public Card retrieveContactInfo(Integer ID) {
        String query = "SELECT * FROM " + TABLE_NAME_CONTACT + " WHERE " +
                CONTACT_ID + " = " + ID;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Card card = new Card();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                card.setCardId(cursor.getInt(0));
                card.setName(cursor.getString(cursor.getColumnIndex(CONTACT_NAME)));
                card.setOrganization(cursor.getString(cursor.getColumnIndex(CONTACT_ORGANIZATION)));
                card.setEmailId(cursor.getString(cursor.getColumnIndex(CONTACT_EMAIL)));
                card.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CONTACT_NUMBER)));
                card.setNotes(cursor.getString(cursor.getColumnIndex(CONTACT_NOTES)));
                card.setFileName(cursor.getString(cursor.getColumnIndex(FILE_NAME)));
            }
            cursor.close();
        }
        return card;
    }

    /**
     * Fetches all contacts saved by current app user. Used to list cards.
     *
     * @param userEmail emailId of app user
     * @return list of cards current app user has stored
     */
    public List<Card> retrieveAllCardsInfo(String userEmail) {
        String query = "SELECT * FROM Contact_Info WHERE " + USER_EMAIL + "= '" + userEmail + "'";
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<Card> cards = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Card card = new Card();
                card.setCardId(cursor.getInt(0));
                card.setName(cursor.getString(cursor.getColumnIndex(CONTACT_NAME)));
                card.setOrganization(cursor.getString(cursor.getColumnIndex(CONTACT_ORGANIZATION)));
                card.setEmailId(cursor.getString(cursor.getColumnIndex(CONTACT_EMAIL)));
                card.setPhoneNumber(cursor.getString(cursor.getColumnIndex(CONTACT_NUMBER)));
                card.setNotes(cursor.getString(cursor.getColumnIndex(CONTACT_NOTES)));
                card.setFileName(cursor.getString(cursor.getColumnIndex(FILE_NAME)));
                cards.add(card);
            }
            cursor.close();
        }
        return cards;
    }

    /**
     * @param cardId Id of current Card
     * @param notes  new notes value
     * @return number of rows affected else -1 in case of exception
     */
    public int updateNotes(int cardId, String notes) {
        try {
            String[] args = new String[]{String.valueOf(cardId)};
            ContentValues newValues = new ContentValues();
            newValues.put(CONTACT_NOTES, notes);
            return db.update(TABLE_NAME_CONTACT, newValues, CONTACT_ID + "=?", args);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Fetch notes of provided card id
     *
     * @param cardId current cardId
     * @return Notes
     */
    public String retrieveNotes(int cardId) {
        String query = "SELECT * FROM Contact_Info WHERE " +
                CONTACT_ID + " = '" + cardId + "'";
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String notes = "";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                notes = cursor.getString(cursor.getColumnIndex(CONTACT_NOTES));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return notes;
    }

    //method call to insert User records to the db
    public long insert(String firstName, String lastName, String emailId, String password) {
        ContentValues content = new ContentValues();
        content.put(FIRST_NAME, firstName);
        content.put(LAST_NAME, lastName);
        content.put(EMAIL_ID, emailId);
        content.put(PASSWORD, password);
        return db.insertOrThrow(TABLE_NAME_CUSTOMER, null, content);
    }

    //method call to insert User records to the db
    public long insertCard(String name, String organization, String contactEmail, String contactNumber,
                           String fileName, String userEmail, String notes) {
        ContentValues content = new ContentValues();
        content.put(CONTACT_NAME, name);
        content.put(CONTACT_ORGANIZATION, organization);
        content.put(CONTACT_EMAIL, contactEmail);
        content.put(CONTACT_NUMBER, contactNumber);
        content.put(FILE_NAME, fileName);
        content.put(USER_EMAIL, userEmail);
        content.put(CONTACT_NOTES, notes);
        return db.insertOrThrow(TABLE_NAME_CONTACT, null, content);
    }

    public void deleteCard(int cardId) {
        String[] args = new String[]{String.valueOf(cardId)};
        db.delete(TABLE_NAME_CONTACT, CONTACT_ID + "=?", args);
    }

    //method call for the sign up validation if the user already exists
    public String retrieveCustomerInfo(String emailId) {
        String existingEmailId = "";
        String query = "SELECT * FROM Customer_Info WHERE EmailId='" + emailId + "'";
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            existingEmailId = cursor.getString(2);
            cursor.close();
        }
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

        DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(TABLE_CREATE_CUSTOMER);
                db.execSQL(TABLE_CREATE_CONTACT);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Customer_Info");
            db.execSQL("DROP TABLE IF EXISTS Contact_Info");
            onCreate(db);
        }
    }
}
