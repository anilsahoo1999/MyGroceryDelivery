package com.aks.mygrocery.utils

import android.content.Context
import java.util.*

class SharedPreference(private val context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)


    fun saveDetailsToSharedPref(keyName: String, valueName: String) {
        val editor = sharedPreferences.edit()
        editor.putString(keyName, valueName).apply()
    }

    fun fetchDetailsFromSharedPref(keyName: String): String? {
        return sharedPreferences.getString(keyName, "")
    }

    fun saveBooleanDataToSharedPref(keyName: String, valueName: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(keyName, valueName).apply()
    }

    fun fetchBooleanDataFromSharedPref(keyName: String): Boolean? {
        return sharedPreferences.getBoolean(keyName, false)
    }

    fun clearDetailsToSharedPreference(): Boolean {
        sharedPreferences.edit().clear().apply()
        return true
    }
}