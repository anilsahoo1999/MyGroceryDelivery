package com.aks.mygrocery.utils

import com.aks.mygrocery.models.CartItemModel
import com.aks.mygrocery.models.ProductModel

object Constants {
    const val USER_ID="userId"
    const val USERNAME="username"
    const val CUSTOMER_ID="customerId"
    const val USER_MAIL_ID="userMailId"
    const val PROFILE_LINK="profileLink"
    const val MOBILE_NO="mobileNo"
    const val IS_LOGIN_DONE="isLogin"
    const val IS_PROFILE_DONE="isProfile"
    const val TYPE="type"

    const val adminId = "bcI5ARwAoHMLLQGdIXlHILEnlZ63"

    var productList : ArrayList<ProductModel>?=null
    var allProductList = arrayListOf<ProductModel>()
    var cartList = arrayListOf<CartItemModel>()

}