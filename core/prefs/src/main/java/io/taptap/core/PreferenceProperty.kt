package io.taptap.core

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferenceProperty<T>(
    private val default: T,
    private val key: String,
    private val sharedPreferences: SharedPreferences
) : ReadWriteProperty<Any, T> {

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    override operator fun getValue(
        thisRef: Any,
        property: KProperty<*>
    ): T = sharedPreferences.run {
        when (default) {
            is String -> getString(key, default)
            is Int -> getInt(key, default)
            is Long -> getLong(key, default)
            is Float -> getFloat(key, default)
            is Boolean -> getBoolean(key, default)
            else -> throwTypeException(default)
        }
    } as T

    override operator fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: T
    ): Unit = sharedPreferences.edit().run {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
            else -> throwTypeException(value)
        }
    }.apply()

    private fun throwTypeException(value: Any?): Nothing {
        val valueType = value?.javaClass?.canonicalName
        throw ClassCastException("PreferenceProperty does not supports type=$valueType")
    }
}