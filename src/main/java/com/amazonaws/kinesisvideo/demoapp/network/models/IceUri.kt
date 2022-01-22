package com.amazonaws.kinesisvideo.demoapp.network.models

import com.google.gson.annotations.SerializedName

data class IceUri(
    @SerializedName("turn_uri_list") val turnUriList: ArrayList<IceUriElement>,
    @SerializedName("stun_uri") val stunUri: String? = ""
)