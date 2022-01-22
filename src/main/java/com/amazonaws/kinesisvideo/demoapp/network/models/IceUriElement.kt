package com.amazonaws.kinesisvideo.demoapp.network.models

import com.google.gson.annotations.SerializedName

data class IceUriElement(
    @SerializedName("password") val password: String? = "",
    @SerializedName("ttl") val ttl: Int,
    @SerializedName("uris") val uris: ArrayList<String>,
    @SerializedName("username") val username: String? = ""
)
