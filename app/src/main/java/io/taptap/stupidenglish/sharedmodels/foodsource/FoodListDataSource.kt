package io.taptap.stupidenglish.sharedmodels.foodsource

import io.taptap.stupidenglish.sharedmodels.foodsource.api.ListApi
import io.taptap.stupidenglish.sharedmodels.foodsource.response.FoodCategoriesResponse
import io.taptap.stupidenglish.sharedmodels.WordItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodListDataSource @Inject constructor(
    private val listApi: ListApi
) : IFoodDataSource {
    private val cachedCategories: List<WordItem>? = null

    override suspend fun getWordList(): List<WordItem> {
        var cachedCategories = cachedCategories
        if (cachedCategories == null) {
            cachedCategories = listApi.getFoodCategories().mapCategoriesToItems()
            cachedCategories = cachedCategories
        }
        return cachedCategories
    }

    private fun FoodCategoriesResponse.mapCategoriesToItems(): List<WordItem> {
        return this.categories.map { category ->
            WordItem(
                id = category.id,
                value = category.name
            )
        }
    }
}