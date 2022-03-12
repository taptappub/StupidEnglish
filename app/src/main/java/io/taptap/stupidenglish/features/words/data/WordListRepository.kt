package io.taptap.stupidenglish.features.words.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.mapper.toGroups
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListRepository @Inject constructor(
    randomWordsDataSource: RandomWordsDataSource,
    private val wordDao: WordDao,
    private val settings: Settings
) : IRandomWordsDataSource by randomWordsDataSource {

    var isSentenceMotivationShown: Boolean
        get() {
            return settings.isSentenceMotivationShown
        }
        set(value) {
            settings.isSentenceMotivationShown = value
        }

    var currentColorIndex: Int
        get() {
            return settings.currentColorIndex
        }
        set(value) {
            settings.currentColorIndex = value
        }

    suspend fun observeWordList(): Reaction<Flow<List<Word>>> = Reaction.on {
        wordDao.observeWords()
            .map { wordDtos ->
                wordDtos.toWords()
            }
    }

    suspend fun observeGroupList(): Reaction<Flow<List<Group>>> = Reaction.on {
        wordDao.observeGroups()
            .map { groupDtos ->
                groupDtos.toGroups()
            }
    }

    suspend fun deleteWord(id: Long): Reaction<Unit> = Reaction.on {
        wordDao.deleteWord(id)
    }

    suspend fun saveGroup(group: String): Reaction<Long> = Reaction.on {
        wordDao.insertGroup(GroupDto(name = group))
    }
}
