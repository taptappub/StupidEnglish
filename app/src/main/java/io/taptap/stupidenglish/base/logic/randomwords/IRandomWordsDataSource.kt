package io.taptap.stupidenglish.base.logic.randomwords

import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction

interface IRandomWordsDataSource {
    suspend fun getRandomWords(maxCount: Int): Reaction<List<Word>>
}