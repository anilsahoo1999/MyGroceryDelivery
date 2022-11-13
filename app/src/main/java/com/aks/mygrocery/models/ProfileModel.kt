package com.aks.mygrocery.models

import java.io.Serializable

data class ProfileModel(
    val userId: String? = null,
    val userToken: String? = null,
    val username: String? = null,
    val userMailId: String? = null,
    val userAddress: String? = null,
    val userLatLng: String? = null,
    val photoUrl: String? = null,
    val userMobileNo: String? = null,
):Serializable
