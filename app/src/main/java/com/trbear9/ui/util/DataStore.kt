package com.trbear9.ui.util

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
    lateinit var pref: SharedPreferences
        private set
    internal fun init(context: Context) {
//        if (hasInited) throw IllegalStateException("Public data storage been initialised!")
        pref = context.getSharedPreferences("settings_prefs", MODE_PRIVATE)
        hasInited = true
        isCompleteTanah = pref.getBoolean("coachTanah", false)
        isCompleteTanaman = pref.getBoolean("coachTanaman", false)
        isCompletePupuk = pref.getBoolean("coachPupuk", false)
        firstTime = pref.getBoolean("firstTime", true)
    }

    val globalKeys = listOf(
        "coachTanah",
        "coachTanaman",
        "coachPupuk",
        "firstTime"
    )

    val guideKey = setOf<String>()

    internal fun getPref(): SharedPreferences? = pref

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

    internal fun createSet(name: String, value: Set<String>) {
        pref.edit { putStringSet(name, value) }
    }

    internal fun addSetElement(name: String, value: String){
        pref.edit {
            addSetElement(name, value)
        }
    }
    fun contains(name: String) : Boolean = pref.contains(name)


    // ========================== Main Activity ==============================
    /** A [Boolean] indicate if the app is fresh or not*/
    internal var firstTime = true
    fun completeFirstTime() {
        firstTime = false
        pref.edit { putBoolean("firstTime", false) }
    }
    // =========================== Guide ================================
    /** A [Boolean] */
    internal var isCompleteTanah = true
    fun completeTanah() {
        isCompleteTanah = true
        pref.edit { putBoolean("coachTanah", true) }
    }

    /** A [Boolean] */
    internal var isCompleteTanaman = true
    fun completeTanaman() {
        isCompleteTanaman = true
        pref.edit { putBoolean("coachTanaman", true) }
    }

    /** A [Boolean] */
    internal var isCompletePupuk = true
    fun completePupuk() {
        isCompletePupuk = true
        pref.edit { putBoolean("coachPupuk", true) }
    }
}