package com.amazonaws.kinesisvideo.demoapp.network.models

import com.google.gson.annotations.SerializedName

data class UserLoginBEModel(
    @SerializedName("data") var data : HualaiToken
)
