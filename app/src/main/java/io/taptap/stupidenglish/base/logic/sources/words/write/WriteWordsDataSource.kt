package io.taptap.stupidenglish.base.logic.sources.words.write

import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.database.dto.WordWithGroupsDto
import io.taptap.stupidenglish.base.model.WordWithGroups
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WriteWordsDataSource @Inject constructor(
    private val wordDao: WordDao
) : IWriteWordsDataSource {

    override suspend fun saveWord(
        word: String,
        description: String,
        groupsIds: List<Long>
    ): Reaction<Unit> = Reaction.on {
        wordDao.insertWordsWithGroups(
            groupsIds = groupsIds,
            words = listOf(
                WordDto(
                    word = word,
                    description = description,
                    points = 0
                )
            )
        )
    }

    override suspend fun saveWords(words: List<WordWithGroups>): Reaction<Unit> =
        Reaction.on {
            val wordWithGroupsDtos = words.map {
                WordWithGroupsDto(
                    word = WordDto(
                        id = it.word.id,
                        word = it.word.word,
                        description = it.word.description,
                        points = it.word.points
                    ),
                    groups = it.groups.map { group ->
                        GroupDto(
                            id = group.id,
                            name = group.name,
                            index = group.index
                        )
                    }
                )
            }
            wordDao.insertWordsWithGroups(wordWithGroupsDtos)
        }

    override suspend fun deleteWord(id: Long): Reaction<Unit> = Reaction.on {
        wordDao.deleteWord(id)
    }

    override suspend fun deleteWords(list: List<Long>): Reaction<Unit> = Reaction.on {
        wordDao.deleteWords(list)
    }
}