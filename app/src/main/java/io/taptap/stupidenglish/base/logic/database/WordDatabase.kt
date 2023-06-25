package io.taptap.stupidenglish.base.logic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.taptap.stupidenglish.base.logic.database.dao.UserDao
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.UserDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.database.dto.WordGroupCrossRef

@Database(
    entities = [
        WordDto::class,
        SentenceDto::class,
        GroupDto::class,
        UserDto::class,
        WordGroupCrossRef::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
abstract class WordDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    abstract fun userDao(): UserDao
}
