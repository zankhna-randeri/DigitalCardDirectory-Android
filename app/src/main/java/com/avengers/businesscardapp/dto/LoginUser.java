package com.avengers.businesscardapp.dto;

public class LoginUser {

    String emailId;
    String password;

    public LoginUser() {
    }

    public LoginUser(String emailId, String password) {
        super();
        this.emailId = emailId;
        this.password = password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
