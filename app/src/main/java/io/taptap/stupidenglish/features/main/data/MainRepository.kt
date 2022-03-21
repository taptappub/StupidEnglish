package io.taptap.stupidenglish.features.main.data

import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    randomWordsDataSource: RandomWordsDataSource,
    private val settings: Settings
) : IRandomWordsDataSource by randomWordsDataSource {

    var isFirstStart: Boolean
        get() {
            return settings.isFirstStart
        }
        set(value) {
            settings.isFirstStart = value
        }
}
