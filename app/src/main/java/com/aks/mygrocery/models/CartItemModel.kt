package com.aks.mygrocery.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CartItemModel(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("quantity")
    @Expose
    var quantity: Int? = null,

    @SerializedName("totalPrice")
    @Expose
    var totalPrice: String? = null,

    @SerializedName("productModel")
    @Expose
    val productModel: ProductModel? = null,

    @SerializedName("selectedPrice")
    var selectedPrice: PricePerKgModel? = null
) : Serializable