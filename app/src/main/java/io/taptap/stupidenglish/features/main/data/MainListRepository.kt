package io.taptap.stupidenglish.features.main.data

import io.taptap.stupidenglish.sharedmodels.foodsource.FoodListDataSource
import io.taptap.stupidenglish.sharedmodels.foodsource.IFoodDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainListRepository @Inject constructor(
    private val foodListDataSource: FoodListDataSource
) : IFoodDataSource by foodListDataSource