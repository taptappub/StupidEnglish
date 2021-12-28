package io.taptap.stupidenglish.features.main.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val wordDao: WordDao
) {

    suspend fun getWordsCount(): Reaction<Int> = Reaction.on {
        wordDao.getWords().size
    }

    suspend fun observeWordsCount(): Reaction<Flow<List<Word>>> = Reaction.on {
        wordDao.observeWords().map { it.toWords() }
    }
}
