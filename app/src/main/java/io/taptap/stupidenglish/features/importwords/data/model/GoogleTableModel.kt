package io.taptap.stupidenglish.features.importwords.data.model

data class GoogleTableModel(
    val majorDimension: String,
    val range: String,
    val values: List<List<String>>
)
