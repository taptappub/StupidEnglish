package io.taptap.stupidenglish.base.logic.database.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "GroupTable"
)
data class GroupDto(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "groupId") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "index") val index: Int
)
