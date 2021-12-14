package io.taptap.stupidenglish.sharedmodels.foodsource

import io.taptap.stupidenglish.sharedmodels.WordItem

interface IFoodDataSource {
    suspend fun getWordList(): List<WordItem>
}