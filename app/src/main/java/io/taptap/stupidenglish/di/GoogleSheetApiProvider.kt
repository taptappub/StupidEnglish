package io.taptap.stupidenglish.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import io.taptap.network.logging.addHttpLoggingInterceptor
import io.taptap.stupidenglish.features.importwords.data.network.IGoogleSheetApi
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GoogleSheetApiProvider {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .apply {
            retryOnConnectionFailure(true)
            addHttpLoggingInterceptor()
        }.build()

    @Provides
    @Singleton
    fun provideGSON(): Gson = GsonBuilder()
        .apply { setLenient().create() }
        .create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): Converter.Factory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideGoogleSheetApi(client: OkHttpClient, converterFactory: Converter.Factory): IGoogleSheetApi {
        val host = "https://sheets.googleapis.com/v4/spreadsheets/"

        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(converterFactory)
            .baseUrl(host)
            .build()
            .create(IGoogleSheetApi::class.java)
    }
}