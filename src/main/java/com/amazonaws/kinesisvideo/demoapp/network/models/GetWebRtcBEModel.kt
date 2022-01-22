package com.amazonaws.kinesisvideo.demoapp.network.models

import com.google.gson.annotations.SerializedName

data class GetWebRtcBEModel(
    @SerializedName("code") val code: String? = "",
    @SerializedName("response_id") val response: String? = "",
    @SerializedName("data") val data: WebRtcData
)
