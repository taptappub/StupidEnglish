package io.taptap.stupidenglish.base.logic.database.dao

import androidx.room.*
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    //-----------------Word-----------------

    @Insert
    suspend fun insertWord(word: WordDto): Long

    @Update
    suspend fun updateWord(word: WordDto)

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
}
