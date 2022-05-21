package io.taptap.stupidenglish.base.logic.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import io.taptap.stupidenglish.base.logic.database.dao.UserDao
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.UserDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto

@Database(
    entities = [
        WordDto::class,
        SentenceDto::class,
        GroupDto::class,
        UserDto::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        ),
        AutoMigration(
            from = 2,
            to = 3
        ),
    ]
)
abstract class WordDatabase : RoomDatabase() {
//    class Migration_1_2 : AutoMigrationSpec

    abstract fun wordDao(): WordDao

    abstract fun userDao(): UserDao
}