package io.taptap.stupidenglish.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.taptap.stupidenglish.base.api.DetailsApi
import io.taptap.stupidenglish.sharedmodels.foodsource.FoodListDataSource
import io.taptap.stupidenglish.sharedmodels.foodsource.IFoodDataSource
import io.taptap.stupidenglish.sharedmodels.foodsource.api.ListApi
import io.taptap.stupidenglish.sharedmodels.foodsource.api.ListApi.Companion.API_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class StupidApiProvider {

    @Provides
    @Singleton
    fun provideAuthInterceptorOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit
            .Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(API_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideListApiService(retrofit: Retrofit): ListApi.Service {
        return retrofit.create(ListApi.Service::class.java)
    }

    @Provides
    @Singleton
    fun provideDetailsApiService(retrofit: Retrofit): DetailsApi.Service {
        return retrofit.create(DetailsApi.Service::class.java)
    }

    @Provides
    @Singleton
    fun provideFoodSource(listApi: ListApi): IFoodDataSource {
        return FoodListDataSource(listApi)
    }
}