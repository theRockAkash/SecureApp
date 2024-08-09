package com.secure.app.retrofit

import android.content.Context
import com.secure.app.util.KeyUtils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @Created by akash on 11/21/2023.
 * Know more about author on https://akash.cloudemy.in
 */
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun getRetrofitApi( @ApplicationContext context: Context): Api {

        val httpClient = OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)

        return Retrofit.Builder()
            .baseUrl(KeyUtils.baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build().create(Api::class.java)
    }


}

fun ResponseBody?.getErrorMessage( ):String {
    return if (this != null) {
        try {
            val errorObj = JSONObject(this.charStream().readText())
            errorObj.getString("key")
        } catch (e: Exception) {
            e.printStackTrace()
            "Error Occurred"
        }
    } else {
        "Error"
    }
}