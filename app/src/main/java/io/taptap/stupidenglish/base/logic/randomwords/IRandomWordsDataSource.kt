package io.taptap.stupidenglish.base.logic.randomwords

import io.taptap.stupidenglish.base.model.Word
import io.taptap.uikit.group.NoGroup
import taptap.pub.Reaction

interface IRandomWordsDataSource {
    suspend fun getRandomWords(
        maxCount: Int,
        group: Long = NoGroup.id
    ): Reaction<List<Word>>
}
