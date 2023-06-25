package io.taptap.stupidenglish.base.model

data class GroupWithWords(
    val group: Group?, //for words without group
    val words: List<Word>
)
