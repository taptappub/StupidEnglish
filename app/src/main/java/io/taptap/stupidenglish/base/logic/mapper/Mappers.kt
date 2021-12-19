package io.taptap.stupidenglish.base.logic.mapper

import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import io.taptap.stupidenglish.base.model.Sentence
import io.taptap.stupidenglish.base.model.Word

fun WordDto.toWord(): Word {
    return Word(
        id = id,
        word = word,
        description = description
    )
}

fun List<WordDto>.toWords(): List<Word> {
    return map { it.toWord() }
}

fun SentenceDto.toSentence(): Sentence {
    return Sentence(
        id = id,
        sentence = sentence
    )
}

fun List<SentenceDto>.toSentences(): List<Sentence> {
    return map { it.toSentence() }
}