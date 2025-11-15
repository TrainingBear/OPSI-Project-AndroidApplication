package com.trbear9.openfarm.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Used for public storage/data for this apk
 * <p>
 * TODO tolong diriviw, takutny salah gw :moyai:
 */
object DataStore {
    private var hasInited: Boolean = false
    private lateinit var pref: SharedPreferences
    internal fun init(context: Context) {
        if (hasInited) throw IllegalStateException("Public data storage been initialised!")
        pref = context.getSharedPreferences("settings_prefs", MODE_PRIVATE)
        hasInited = true
    }

    internal fun getPref(): SharedPreferences? = pref

    internal fun getBoolean(name: String) = pref.getBoolean(name, map[name] as Boolean)
    internal fun getString(name: String) = pref.getString(name, map[name] as String)
    internal fun getInt(name: String) = pref.getInt(name, map[name] as Int)
    internal fun getLong(name: String) = pref.getLong(name, map[name] as Long)
    internal fun getFloat(name: String) = pref.getFloat(name, map[name] as Float)
    internal fun getStringSet(name: String) = pref.getStringSet(
        name,
        map[name] as Set<String>?
    )

    internal fun setBoolean(name: String, value: Boolean) {
        pref.edit { putBoolean(name, value) }
    }

    internal fun setString(name: String, value: String) {
        pref.edit { putString(name, value) }
    }

    internal fun setInt(name: String, value: Int) {
        pref.edit { putInt(name, value) }
    }

    internal fun setLong(name: String, value: Long) {
        pref.edit { putLong(name, value) }
    }

    internal fun setFloat(name: String, value: Float) {
        pref.edit { putFloat(name, value) }
    }

    internal fun setStringSet(name: String, value: Set<String>) {
        pref.edit { putStringSet(name, value) }
    }


    // ========================== Main Activity ==============================
    /** A [Boolean] indicate if the app is fresh or not*/
    internal val firstTime = "firstTime"
    // =========================== Guide ================================
    /** A [Boolean] */
    internal val isCompleteTanah = "isCompleteTanah"

    /** A [Boolean] */
    internal val isCompleteTanaman = "isCompleteTanaman"

    /** A [Boolean] */
    internal val isCompletePupuk = "isCompletePupuk"

    val map: Map<String, Any> = mapOf(
        firstTime to true,
        isCompleteTanah to false,
        isCompleteTanaman to false,
        isCompletePupuk to false
    )

}