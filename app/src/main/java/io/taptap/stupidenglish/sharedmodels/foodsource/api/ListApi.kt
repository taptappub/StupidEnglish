package io.taptap.stupidenglish.sharedmodels.foodsource.api

import io.taptap.stupidenglish.sharedmodels.foodsource.response.FoodCategoriesResponse
import io.taptap.stupidenglish.sharedmodels.foodsource.response.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListApi @Inject constructor(private val service: Service) {

    suspend fun getFoodCategories(): FoodCategoriesResponse = service.getFoodCategories()

    interface Service {
        @GET("categories.php")
        suspend fun getFoodCategories(): FoodCategoriesResponse
    }

    companion object {
        const val API_URL = "https://www.themealdb.com/api/json/v1/1/"
    }
}


