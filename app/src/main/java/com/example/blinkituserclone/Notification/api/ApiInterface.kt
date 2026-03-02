package com.example.blinkituserclone.Notification.api

import com.example.blinkituserclone.Notification.api.models.FCMRequest
import com.example.blinkituserclone.Notification.api.models.FCMResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @POST("v1/projects/blinkitclone-fdfc4/messages:send")
    @Headers("Content-Type: application/json")
    suspend fun sendNotification(
        @Header("Authorization") bearerToken : String,
        @Body request: FCMRequest
    ) : FCMResponse


}