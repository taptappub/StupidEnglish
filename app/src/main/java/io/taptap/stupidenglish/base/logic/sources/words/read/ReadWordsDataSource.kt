package io.taptap.stupidenglish.base.logic.sources.words.read

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.logic.mapper.toWordWithGroups
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.base.model.WordWithGroups
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IReadWordsDataSource {

    override suspend fun observeWordList(groupId: Long): Reaction<Flow<List<Word>>> = Reaction.on {
        if (groupId == NoGroup.id) {
            wordDao.observeWords()
                .map { wordDtos ->
                    wordDtos.toWords()
                }
        } else {
            wordDao.observeGroupWithWords(groupId)
                .map {
                    it.words.toWords()
                }
        }
    }

    override suspend fun getWordList(
        groupId: Long
    ): Reaction<List<Word>> = Reaction.on {
        if (groupId == NoGroup.id) {
            wordDao.getWords()
                .toWords()
        } else {
            wordDao.getGroupWithWords(groupId)
                .words
                .toWords()
        }
    }

    override suspend fun getWordWithGroups(wordId: Long): Reaction<WordWithGroups> = Reaction.on {
        wordDao.getWordWithGroups(wordId).toWordWithGroups()
    }

    override suspend fun getWordsById(wordsIds: List<Long>): Reaction<List<Word>> = Reaction.on {
        wordsIds.map {
            wordDao.getWordDto(it)?.toWord()
                ?: error("There is no word with id = $it")
        }
    }
}
