package io.taptap.stupidenglish.features.alarm.data

import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    randomWordsDataSource: RandomWordsDataSource
) : IRandomWordsDataSource by randomWordsDataSource