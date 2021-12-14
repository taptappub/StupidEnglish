package io.taptap.stupidenglish.features.details.data

import io.taptap.stupidenglish.base.api.DetailsApi
import io.taptap.stupidenglish.sharedmodels.foodsource.response.MealsResponse
import io.taptap.stupidenglish.sharedmodels.WordItem
import io.taptap.stupidenglish.sharedmodels.foodsource.IFoodDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StupidWordRepository @Inject constructor(
    private val detailsApi: DetailsApi,
    private val foodListDataSource: IFoodDataSource
) : IFoodDataSource by foodListDataSource {

    suspend fun getMealsByCategory(categoryId: String): List<WordItem> {
        val categoryName = getWordList().first { it.id == categoryId }.value
        return detailsApi.getMealsByCategory(categoryName).mapMealsToItems()
    }

    private fun MealsResponse.mapMealsToItems(): List<WordItem> {
        return this.meals.map { category ->
            WordItem(
                id = category.id,
                value = category.name
            )
        }
    }
}