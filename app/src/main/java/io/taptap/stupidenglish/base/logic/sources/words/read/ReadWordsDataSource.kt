package io.taptap.stupidenglish.base.logic.sources.words.read

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.convertGroupsIds
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.model.Word
import io.taptap.uikit.group.NoGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import taptap.pub.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IReadWordsDataSource {

    override suspend fun observeWordList(groupId: Long): Reaction<Flow<List<Word>>> = Reaction.on {
        wordDao.observeWords()
            .map { wordDtos ->
                val wordsByGroup = if (groupId == NoGroup.id) {
                    wordDtos
                } else {
                    wordDtos.filter {
                        it.groupsIds.convertGroupsIds().contains(groupId)
                    }
                }

                wordsByGroup.toWords()
            }
    }

    override suspend fun getWordList(
        groupId: Long
    ): Reaction<List<Word>> = Reaction.on {
        val words = wordDao.getWords()
        val wordsByGroup = if (groupId == NoGroup.id) {
            words
        } else {
            words.filter {
                it.groupsIds.convertGroupsIds().contains(groupId)
            }
        }
        wordsByGroup.toWords()
    }

    override suspend fun getWordsCountInGroup(groupId: Long): Reaction<Int> {
        return getWordList(groupId).map { it.size }
    }

    override suspend fun getWordsById(wordsIds: List<Long>): Reaction<List<Word>> = Reaction.on {
        wordsIds.map {
            wordDao.getWordDto(it)?.toWord()
                ?: error("There is no word with id = $it")
        }
    }
}
