package io.taptap.stupidenglish.base.api

import io.taptap.stupidenglish.sharedmodels.foodsource.response.FoodCategoriesResponse
import io.taptap.stupidenglish.sharedmodels.foodsource.response.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailsApi @Inject constructor(private val service: Service) {

    suspend fun getMealsByCategory(categoryId: String): MealsResponse =
        service.getMealsByCategory(categoryId)

    interface Service {

        @GET("filter.php")
        suspend fun getMealsByCategory(@Query("c") categoryId: String): MealsResponse
    }

    companion object {
        const val API_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
}


