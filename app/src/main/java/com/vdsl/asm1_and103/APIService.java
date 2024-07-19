package com.vdsl.asm1_and103;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
     String DOMAIN = "http://172.20.10.4:3030/";
    @GET("/api/list")
    Call<List<foodModel>> getFoods();

    @POST("/api/add")
    Call<foodModel> addFood(@Body foodModel food);

    @PUT("/api/update/{id}")
    Call<foodModel> updateFood(@Path("id") String id, @Body foodModel food);

    @DELETE("/api/delete/{id}")
    Call<Void> deleteFood(@Path("id") String id);
}
