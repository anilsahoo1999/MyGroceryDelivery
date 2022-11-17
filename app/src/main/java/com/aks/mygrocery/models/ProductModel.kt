package com.aks.mygrocery.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductModel(
    @Expose
    @SerializedName("imageUrl")
    var imageUrl: String? = null,
    @Expose
    @SerializedName("name")
    var name: String? = null,
    @Expose
    @SerializedName("productID")
    var productID: String? = null,
    @Expose
    @SerializedName("categoryID")
    var categoryID: String? = null,
    @Expose
    @SerializedName("categoryName")
    var categoryName: String? = null,
    @Expose
    @SerializedName("subCategoryID")
    var subCategoryID: String? = null,
    @Expose
    @SerializedName("priceList")
    var priceList: List<PricePerKgModel>? = null,
    @Expose
    @SerializedName("brandName")
    var brandName: String? = null,
    @Expose
    @SerializedName("isSellingFast")
    var isSellingFast: Boolean? = false,
    @Expose
    @SerializedName("isProductOutOfStock")
    var isProductOutOfStock: Boolean? = false
) : Serializable
