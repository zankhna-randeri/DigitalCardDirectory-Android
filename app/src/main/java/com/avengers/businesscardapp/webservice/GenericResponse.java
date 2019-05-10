package com.avengers.businesscardapp.webservice;

public class GenericResponse {

    private String message;
    private Integer responseCode;

    public GenericResponse() {
    }

    public GenericResponse(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }
}
