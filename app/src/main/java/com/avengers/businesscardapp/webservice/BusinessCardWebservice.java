package com.avengers.businesscardapp.webservice;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BusinessCardWebservice {

    //    String BASE_URL = "https://opentdb.com/";
    String BASE_URL = "http://localhost:8080/";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("/getCloudFrontUrl")
    Call<GenericResponse> getCardUrl();

}
