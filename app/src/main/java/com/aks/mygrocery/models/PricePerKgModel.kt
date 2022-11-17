package com.aks.mygrocery.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PricePerKgModel(
    @Expose
    @SerializedName("id")
    val id: Int? = null,
    @Expose
    @SerializedName("gram")
    val gram: String? = null,
    @Expose
    @SerializedName("price")
    val price: String? = null,
    @Expose
    @SerializedName("productStatus")
    var productStatus: ProductStatus? = null
) : Serializable

