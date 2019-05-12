package com.avengers.businesscardapp.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class UploadCardResponse implements Parcelable {

    private String name;
    private String organization;
    private String emailId;
    private String number;

    protected UploadCardResponse(Parcel in) {
        name = in.readString();
        organization = in.readString();
        emailId = in.readString();
        number = in.readString();
    }

    public static final Creator<UploadCardResponse> CREATOR = new Creator<UploadCardResponse>() {
        @Override
        public UploadCardResponse createFromParcel(Parcel in) {
            return new UploadCardResponse(in);
        }

        @Override
        public UploadCardResponse[] newArray(int size) {
            return new UploadCardResponse[size];
        }
    };

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(organization);
        dest.writeString(emailId);
        dest.writeString(number);
    }

    @Override
    public String toString() {
        return "UploadCardResponse{" +
                "name='" + name + '\'' +
                ", organization='" + organization + '\'' +
                ", emailId='" + emailId + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
