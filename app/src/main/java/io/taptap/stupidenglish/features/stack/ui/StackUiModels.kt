package io.taptap.stupidenglish.features.stack.ui

sealed interface StackModels

data class StackItemUI(
    val word: String
) : StackModels
