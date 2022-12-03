package com.aks.mygrocery.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FcmTokenModel(
    @SerializedName("fcmToken")
    @Expose
    val fcmToken : String ?= null,

    @SerializedName("userId")
    @Expose
    val userId : String ?= null,

    @SerializedName("subscribeToTopic")
    @Expose
    val subscribeToTopic : String ?= null

):Serializable
