package io.taptap.stupidenglish.base.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    //-----------------Word-----------------

    @Insert
    suspend fun insertWord(word: WordDto): Long

    @Insert
    suspend fun insertWords(word: List<WordDto>)

    @Update
    suspend fun updateWord(word: WordDto)

    @Query(
        """
        DELETE FROM WordTable 
        WHERE WordTable.id = :wordId
        """
    )
    suspend fun deleteWord(wordId: Long): Int

    @Query(
        """
        SELECT *
        FROM WordTable
        WHERE WordTable.id = :wordId
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
        WHERE WordTable.id in (:wordIds)
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
    suspend fun deleteSentences(sentenceIds: List<Long>)

    //-----------------Group-----------------

    @Insert
    suspend fun insertGroup(group: GroupDto): Long

    @Update
    suspend fun updateGroup(group: GroupDto)

    @Query(
        """
        DELETE FROM GroupTable 
        WHERE GroupTable.id = :groupId
        """
    )
    suspend fun deleteGroup(groupId: Long): Int

    @Query(
        """
        DELETE FROM GroupTable 
        WHERE GroupTable.id in (:groupIds)
        """
    )
    suspend fun deleteGroups(groupIds: List<Long>)

    @Query(
        """
        SELECT *
        FROM GroupTable
        WHERE GroupTable.id = :groupId
        LIMIT 1
        """
    )
    suspend fun getGroupDto(groupId: Long): GroupDto?

    @Query(
        """
        SELECT *
        FROM GroupTable
        """
    )
    fun observeGroups(): Flow<List<GroupDto>>

    @Query(
        """
        SELECT *
        FROM GroupTable
        """
    )
    fun getGroups(): List<GroupDto>
}
