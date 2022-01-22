package com.amazonaws.kinesisvideo.demoapp.network.models

import com.google.gson.annotations.SerializedName

data class HualaiToken(
    @SerializedName("user_center_id") val userCenterId: String? = "",
    @SerializedName("access_token") val accessToken: String? = "",
    @SerializedName("refresh_token") val refreshToken: String? = ""
)
