package com.avengers.businesscardapp.webservice;

import com.avengers.businesscardapp.dto.SignupUser;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface BusinessCardWebservice {

    String BASE_URL = "http://10.0.2.2:8080";
//    String BASE_URL = "http://10.15.232.131:8080";


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @POST("/userSignup")
    Call<GenericResponse> registerUser(@Body SignupUser userRequest);


    @GET("/getCloudFrontUrl")
    Call<GenericResponse> getCardUrl();

    @Multipart
    @POST("/cmpe277AvengersNER")
    Call<GenericResponse> uploadCard
            (@Part MultipartBody.Part file,
             @Part("emailId") RequestBody emailId);

}
