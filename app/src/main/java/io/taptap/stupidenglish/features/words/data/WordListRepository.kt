package io.taptap.stupidenglish.features.words.data

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.mapper.toWord
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.user.read.IReadUserDataSource
import io.taptap.stupidenglish.base.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListRepository @Inject constructor(
    randomWordsDataSource: IRandomWordsDataSource,
    readGroupsDataSource: IReadGroupsDataSource,
    writeGroupsDataSource: IWriteGroupsDataSource,
    readUserDataSource: IReadUserDataSource,
    private val wordDao: WordDao,
    private val settings: Settings
) : IRandomWordsDataSource by randomWordsDataSource,
    IReadGroupsDataSource by readGroupsDataSource,
    IWriteGroupsDataSource by writeGroupsDataSource,
    IReadUserDataSource by readUserDataSource {

    var isSentenceMotivationShown: Boolean
        get() {
            return settings.isSentenceMotivationShown
        }
        set(value) {
            settings.isSentenceMotivationShown = value
        }

    suspend fun observeWordList(): Reaction<Flow<List<Word>>> = Reaction.on {
        wordDao.observeWords()
            .map { wordDtos ->
                wordDtos.toWords()
            }
    }

    fun getWordList(): Reaction<List<Word>> = Reaction.on {
        wordDao.getWords()
            .map { wordDto ->
                wordDto.toWord()
            }
    }

    suspend fun deleteWord(id: Long): Reaction<Unit> = Reaction.on {
        wordDao.deleteWord(id)
    }

    suspend fun removeGroups(groups: List<Long>): Reaction<Unit> = Reaction.on {
        wordDao.deleteGroups(groups)
    }

    suspend fun deleteWords(list: List<Long>): Reaction<Unit> = Reaction.on {
        wordDao.deleteWords(list)
    }
}
