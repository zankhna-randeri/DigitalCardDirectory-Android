package com.avengers.businesscardapp.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {
    private int cardId;
    private String name;
    private String organization;
    private String emailId;
    private String phoneNumber;
    private String notes;
    private String cardName;


    public Card(int cardId, String name, String organization, String emailId,
                String phoneNumber, String notes, String cardName) {
        this.cardId = cardId;
        this.name = name;
        this.organization = organization;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
        this.cardName = cardName;
    }

    protected Card(Parcel in) {
        cardId = in.readInt();
        name = in.readString();
        organization = in.readString();
        emailId = in.readString();
        phoneNumber = in.readString();
        notes = in.readString();
        cardName = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cardId);
        dest.writeString(this.name);
        dest.writeString(this.organization);
        dest.writeString(this.emailId);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.notes);
        dest.writeString(this.cardName);
    }
}
