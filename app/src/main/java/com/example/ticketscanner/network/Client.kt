package com.example.ticketscanner.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface Client {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("gate-login")
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") password: String
    ) : Call<JsonObject>


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("scan-data")
    fun sendBookingDataToServer(
        @Field("data") bookingNumber: String
    ): Call<JsonObject>

}