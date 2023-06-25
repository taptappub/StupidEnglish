package io.taptap.stupidenglish.base.logic.database.dto

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.database.dto.WordGroupCrossRef

data class GroupWithWordsDto(
    @Embedded val group: GroupDto,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "wordId",
        associateBy = Junction(WordGroupCrossRef::class)
    )
    val words: List<WordDto>
)