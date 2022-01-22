package com.amazonaws.kinesisvideo.demoapp.network

import HUALAI_BASE_URL
import HualaiApi
import com.amazonaws.kinesisvideo.demoapp.network.models.IceUri
import com.amazonaws.kinesisvideo.demoapp.network.models.IceUriElement
import com.amazonaws.kinesisvideo.demoapp.network.models.WebRtcData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val TOKEN_HUALAI =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJDcmVhdGVUcyI6MTY0Mjc4MzcxMTMwNSwiRXhwaXJlVHMiOjE2NDMwNDI5MTEzMDUsIklzc3VlciI6Ikdsb2JhbEVkaXRpb24gQXBpIiwiVXNlckNlbnRlcklkIjoiMjI5Mjg1MDUxMzc1MjcxOTM2IiwiQWNjb3VudE5hbWUiOiJtaXJhdmFsbGVzZ0BnbWFpbC5jb20iLCJUb2tlblZlcnNpb24iOiIxIiwiQ2hlY2tDb2RlIjoidjl3d0tOUlg5RSIsIkp0aSI6MTUwOTg5MTc5Mn0.v1L1R2dZJOrVDJKvvEjbZQKeJTP4maDkNCwHxnC4wn8"


class HualaiRetrofitBuilder() {

    private val loggingInterceptor = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val hualaiOkHttpClient = OkHttpClient()
        .newBuilder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val builtHualaiRetrofit = Retrofit.Builder()
        .baseUrl(HUALAI_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(hualaiOkHttpClient)
        .build()

    private val api = builtHualaiRetrofit.create(HualaiApi::class.java)

    fun getWebRTCdata(): WebRtcData {

        return try {

            val jsonObjectData = JSONObject()
            jsonObjectData.put("device_id", "A4DA2220000C")
            jsonObjectData.put("client_id", "229285051375271936")

            val currentTimestamp = System.currentTimeMillis()

            val jsonObject = JSONObject()
            jsonObject.put("app_name", "com.hualai.geniuslife")
            jsonObject.put("request_id", "f3485e42f1dc47e88950500d54fb8521")
            jsonObject.put("timestamp", currentTimestamp)
            jsonObject.put("data", jsonObjectData)
            jsonObject.put("app_version", "1.0.2")
            jsonObject.put("os_name", "Android")
            jsonObject.put("os_version", "4.19.132")
            jsonObject.put("terminal_id", "c80624d4-0fe8-423f-a870-125e3b7a52a7")

            val body = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val result = api.getWebRTC(TOKEN_HUALAI, body)
            result.data
        } catch (e: Exception) {
            WebRtcData(null, IceUri( arrayListOf<IceUriElement>() , ""))
        }

    }

}