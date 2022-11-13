package com.aks.mygrocery.models

import android.hardware.display.DeviceProductInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoryModel(

    @Expose
    @SerializedName("imageUrl")
    val imageUrl : String?=null,
    @Expose
    @SerializedName("name")
    val name : String?=null,
    @Expose
    @SerializedName("categoryID")
    var categoryID: String?=null
):Serializable