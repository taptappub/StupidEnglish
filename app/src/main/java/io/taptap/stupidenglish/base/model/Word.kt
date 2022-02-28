package io.taptap.stupidenglish.base.model

data class Word(
    val id: Long,
    val word: String,
    val description: String,
    val groupsIds: List<Long>,
    val points: Int
)

