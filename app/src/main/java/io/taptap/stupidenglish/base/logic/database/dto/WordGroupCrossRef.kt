package io.taptap.stupidenglish.base.logic.database.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "WordGroupCrossRefTable",
    primaryKeys = ["groupId", "wordId"],
    foreignKeys = [
        ForeignKey(
            entity = GroupDto::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
        ForeignKey(
            entity = WordDto::class,
            parentColumns = ["wordId"],
            childColumns = ["wordId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class WordGroupCrossRef(
    val groupId: Long,
    val wordId: Long
)

