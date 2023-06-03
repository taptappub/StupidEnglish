package io.taptap.stupidenglish.base.logic.mapper

import io.taptap.stupidenglish.base.logic.database.dto.GroupDto
import io.taptap.stupidenglish.base.logic.database.dto.GroupWithWordsDto
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.logic.database.dto.WordWithGroupsDto
import io.taptap.stupidenglish.base.model.Group
import io.taptap.stupidenglish.base.model.GroupWithWords
import io.taptap.stupidenglish.base.model.Sentence
import io.taptap.stupidenglish.base.model.Word
import io.taptap.stupidenglish.base.model.WordWithGroups

fun WordDto.toWord(): Word = Word(
    id = id,
    word = word,
    description = description,
    points = points
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

fun GroupWithWordsDto.toGroupWithWords(): GroupWithWords = GroupWithWords(
    group = Group(
        id = this.group.id,
        name = this.group.name
    ),
    words = this.words.toWords()
)

fun List<GroupDto>.toGroups(): List<Group> = map { it.toGroup() }

fun List<GroupWithWordsDto>.toGroupsWithWords(): List<GroupWithWords> = map { it.toGroupWithWords() }

fun WordWithGroupsDto.toWordWithGroups() = WordWithGroups(
    word = this.word.toWord(),
    groups = this.groups.map { it.toGroup() }
)

