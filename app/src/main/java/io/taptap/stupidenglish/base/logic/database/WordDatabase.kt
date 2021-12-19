package io.taptap.stupidenglish.base.logic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto

@Database(
    entities = [
        WordDto::class,
        SentenceDto::class
    ],
    version = 1,
    exportSchema = true
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}