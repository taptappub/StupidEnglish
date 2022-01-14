package io.taptap.stupidenglish.features.addsentence.navigation

import javax.inject.Singleton

@Singleton
object AddSentenceArgumentsMapper {
    fun mapFrom(value: String): List<Long> {
        return value.split(",").map {
            it.toLong()
        }
    }

    fun mapTo(value: List<Long>): String {
        return value.joinToString(",")
    }
}