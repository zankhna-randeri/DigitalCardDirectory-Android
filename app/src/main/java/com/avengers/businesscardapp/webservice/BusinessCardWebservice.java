package com.avengers.businesscardapp.webservice;

import com.avengers.businesscardapp.dto.GenericResponse;
import com.avengers.businesscardapp.dto.LoginResponse;
import com.avengers.businesscardapp.dto.LoginUser;
import com.avengers.businesscardapp.dto.SignUpUser;
import com.avengers.businesscardapp.dto.UploadCardResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BusinessCardWebservice {

    //    String BASE_URL = "http://10.0.2.2:8080";
//    String BASE_URL = "http://BusinessCardDirectory.us-west-2.elasticbeanstalk.com";
    String BASE_URL = "https://sumanthravipati-sjsu.online";
//    String BASE_URL = "http://10.252.55.140:8080";
//    String BASE_URL = "http://192.168.0.142:8080";
//    String BASE_URL = "http://10.15.232.131:8080";


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @POST("/userSignup")
    Call<GenericResponse> registerUser(@Body SignUpUser userRequest);

    @POST("/userLogin")
    Call<LoginResponse> loginUser(@Body LoginUser userLoginRequest);

    @GET("/getCloudFrontUrl")
    Call<GenericResponse> getCardUrl();

//    @Multipart
//    @POST("/cmpe277AvengersNER")
//    Call<UploadCardResponse> uploadCard
//            (@Part MultipartBody.Part file,
//             @Part("emailId") RequestBody emailId);

    @FormUrlEncoded
    @POST("/cmpe277AvengersNER")
    Call<UploadCardResponse> uploadCard(@Field("fileName") String fileName,
                                        @Field("emailId") String emailId);

    @FormUrlEncoded
    @POST("/businessCardReferral")
    Call<GenericResponse> referCard(@Field("toEmailId") String toEmailId,
                                    @Field("userEmailId") String appUserEmail,
                                    @Field("fileName") String fileName,
                                    @Field("contactName") String contactName,
                                    @Field("contactEmailId") String contactEmail,
                                    @Field("organization") String org,
                                    @Field("contactNumber") String contactNo);

}
