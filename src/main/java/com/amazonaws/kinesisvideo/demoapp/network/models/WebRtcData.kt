package com.amazonaws.kinesisvideo.demoapp.network.models

import com.google.gson.annotations.SerializedName

data class WebRtcData(
    @SerializedName("wss_sign_url") val wssSignUrl: String? = "",
    @SerializedName("ice_uri") val iceUri: IceUri
)
