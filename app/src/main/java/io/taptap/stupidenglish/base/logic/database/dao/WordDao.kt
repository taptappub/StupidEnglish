package io.taptap.stupidenglish.base.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.GroupWithWordsDto
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.database.dto.WordGroupCrossRef
import io.taptap.stupidenglish.base.logic.database.dto.WordWithGroupsDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    //-----------------Word-----------------

    @Insert
    suspend fun insertWord(word: WordDto): Long

    @Query("")
    @Transaction
    suspend fun insertWordsWithGroups(groupsIds: List<Long>, words: List<WordDto>) {
        words.forEach { wordDto ->
            val id = insertWord(wordDto)
            groupsIds.forEach { groudId ->
                insertWordGroupCrossRef(
                    WordGroupCrossRef(
                        groupId = groudId,
                        wordId = id
                    )
                )
            }
        }
    }

    @Query("")
    @Transaction
    suspend fun insertWordsWithGroups(wordsWithGroups: List<WordWithGroupsDto>) {
        wordsWithGroups.forEach { wordWithGroups ->
            val id = insertWord(wordWithGroups.word)
            wordWithGroups.groups.forEach { group ->
                insertWordGroupCrossRef(
                    WordGroupCrossRef(
                        groupId = group.id,
                        wordId = id
                    )
                )
            }
        }
    }

    @Insert
    suspend fun insertWords(word: List<WordDto>)

    @Update
    suspend fun updateWord(word: WordDto)

    @Query(
        """
        DELETE FROM WordTable 
        WHERE WordTable.wordId = :wordId
        """
    )
    suspend fun deleteWord(wordId: Long): Int

    @Query(
        """
        SELECT *
        FROM WordTable
        WHERE WordTable.wordId = :wordId
        LIMIT 1
        """
    )
    suspend fun getWordDto(wordId: Long): WordDto?

    @Query(
        """
        SELECT *
        FROM WordTable
        """
    )
    fun observeWords(): Flow<List<WordDto>>

    @Query(
        """
        SELECT *
        FROM WordTable
        """
    )
    fun getWords(): List<WordDto>

    @Query(
        """
        DELETE FROM WordTable 
        WHERE WordTable.wordId in (:wordIds)
        """
    )
    suspend fun deleteWords(wordIds: List<Long>)

    //-----------------Sentence-----------------

    @Insert
    suspend fun insertSentence(sentence: SentenceDto): Long

    @Query(
        """
        SELECT *
        FROM SentenceTable
        WHERE SentenceTable.id = :sentenceId
        LIMIT 1
        """
    )
    suspend fun getSentenceDto(sentenceId: Long): SentenceDto?

    @Query(
        """
        SELECT *
        FROM SentenceTable
        """
    )
    fun observeSentences(): Flow<List<SentenceDto>>

    @Query(
        """
        SELECT *
        FROM SentenceTable
        """
    )
    fun getSentences(): List<SentenceDto>

    @Query(
        """
        DELETE FROM SentenceTable 
        WHERE SentenceTable.id = :sentenceId
        """
    )
    suspend fun deleteSentence(sentenceId: Long): Int

    @Query(
        """
        DELETE FROM SentenceTable 
        WHERE SentenceTable.id in (:sentenceIds)
        """
    )
    suspend fun removeSentences(sentenceIds: List<Long>)

    //-----------------Group-----------------

    @Query("")
    @Transaction
    suspend fun rearrangeGroups(from: Long, to: Long) {
        val fromGroupDto = getGroup(from)
        val toGroupDto = getGroup(to)
        val newFrom = fromGroupDto.copy(index = toGroupDto.index)
        val newTo = toGroupDto.copy(index = fromGroupDto.index)
//        deleteGroup(from)
//        updateGroup(newTo)
//        insertGroup(newFrom)
//        insertGroup(newFrom)
        updateGroup(newFrom)
        updateGroup(newTo)
    }

    @Query("")
    @Transaction
    suspend fun insertGroup(groupName: String): Long {
        val maxIndex = getGroupsMaxIndex()
        val newGroup = GroupDto(name = groupName, index = maxIndex + 1)
        return insertGroup(newGroup)
    }

    @Insert
    suspend fun insertGroup(group: GroupDto): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGroup(group: GroupDto)

    @Query(
        """
        DELETE FROM GroupTable 
        WHERE GroupTable.groupId = :groupId
        """
    )
    suspend fun deleteGroup(groupId: Long): Int

    @Query(
        """
        DELETE FROM GroupTable 
        WHERE GroupTable.groupId in (:groupIds)
        """
    )
    suspend fun deleteGroups(groupIds: List<Long>)

    @Query(
        """
        SELECT *
        FROM GroupTable
        ORDER by GroupTable.`index` ASC
        """
    )
    fun observeGroups(): Flow<List<GroupDto>>

    @Query(
        """
        SELECT *
        FROM GroupTable
        """
    )
    suspend fun getGroups(): List<GroupDto>

    @Query(
        """
        SELECT *
        FROM GroupTable
        WHERE GroupTable.groupId = :groupId
        """
    )
    suspend fun getGroup(groupId: Long): GroupDto

    @Query(
        """
        SELECT MAX(GroupTable.`index`)
        FROM GroupTable
        """
    )
    suspend fun getGroupsMaxIndex(): Int

    @Query(
        """
        SELECT *
        FROM GroupTable
        WHERE GroupTable.groupId in (:groupIds)
        """
    )
    suspend fun getGroups(groupIds: List<Long>): List<GroupDto>

    //-----------------Group with words-----------------

    @Insert
    suspend fun insertWordGroupCrossRef(wordGroupCrossRef: WordGroupCrossRef)

    @Transaction
    @Query(
        """
        SELECT *
        FROM GroupTable
        WHERE GroupTable.groupId = :groupId
        """
    )
    fun observeGroupWithWords(groupId: Long): Flow<GroupWithWordsDto>

    @Transaction
    @Query(
        """
        SELECT *
        FROM GroupTable
        WHERE GroupTable.groupId = :groupId
        """
    )
    suspend fun getGroupWithWords(groupId: Long): GroupWithWordsDto

    @Transaction
    @Query(
        """
        SELECT *
        FROM WordTable
        WHERE WordTable.wordId = :wordId
        """
    )
    suspend fun getWordWithGroups(wordId: Long): WordWithGroupsDto

    @Query(
        """
        SELECT COUNT(wordId)
        FROM WordGroupCrossRefTable
        WHERE groupId = :groupId
        """
    )
    suspend fun getWordsCountInGroup(groupId: Long): Int
}
