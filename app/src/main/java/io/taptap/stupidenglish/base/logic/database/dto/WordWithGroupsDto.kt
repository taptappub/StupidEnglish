package io.taptap.stupidenglish.base.logic.database.dto

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.database.dto.WordGroupCrossRef

data class WordWithGroupsDto(
    @Embedded val word: WordDto,
    @Relation(
        parentColumn = "wordId",
        entityColumn = "groupId",
        associateBy = Junction(WordGroupCrossRef::class)
    )
    val groups: List<GroupDto>
)