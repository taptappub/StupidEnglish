package io.taptap.stupidenglish.base.logic.mapper

import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.Sentence
import io.taptap.stupidenglish.base.model.Word

fun WordDto.toWord(): Word = Word(
    id = id,
    word = word,
    description = description,
    points = points,
    groupsIds = if (groupsIds.isNullOrEmpty()) {
        emptyList()
    } else if (!groupsIds.contains(",")) {
        listOf(groupsIds.toLong())
    } else {
        groupsIds.split(",").map { it.toLong() }
    }
)

fun List<WordDto>.toWords(): List<Word> = map { it.toWord() }

fun SentenceDto.toSentence(): Sentence = Sentence(
    id = id,
    sentence = sentence
)

fun List<SentenceDto>.toSentences(): List<Sentence> = map { it.toSentence() }

fun GroupDto.toGroup(): Group = Group(
    id = id,
    name = name
)

fun List<GroupDto>.toGroups(): List<Group> = map { it.toGroup() }