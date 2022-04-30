package io.taptap.network.logging

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

private const val HTTP_LOG_NAME = "UnlimintBackendApi"

fun OkHttpClient.Builder.addHttpLoggingInterceptor() {
//    if (BuildConfig.DEBUG) {
        addInterceptor(getHttpLoggingInterceptor())
//    }
}

private fun getHttpLoggingInterceptor() = HttpLoggingInterceptor { message ->
    Log.d(
        HTTP_LOG_NAME,
        message
    )
}.apply {
    level = HttpLoggingInterceptor.Level.BODY
}
