package com.example.ticketscanner.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface Client {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") password: String
    ) : Call<JsonObject>


//    @Headers("Accept: application/json")
//    @FormUrlEncoded
//    @POST("book-ticket")
//    fun sendBookingDataToServer(
//        @Field("bookingData") booking: String
//    ): Call<JsonObject>

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("scan-data")
    fun sendBookingDataToServer(
        @Field("data") booking: String
    ): Call<JsonObject>


}