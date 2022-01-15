package io.taptap.stupidenglish.base.logic.database.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "WordTable"
)
data class WordDto(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "points") val points: Int
)
