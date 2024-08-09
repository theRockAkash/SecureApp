package com.secure.app.retrofit

import com.secure.app.screens.home.Data
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @Created by akash on 11/21/2023.
 * Know more about author on https://akash.cloudemy.in
 */
interface Api {


    @POST("Account/SendSecureData")
    suspend fun sendSecureData(@Body req:Map<String,String>): Response<Data>


}