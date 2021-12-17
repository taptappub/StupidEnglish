package io.taptap.stupidenglish.features.addword.data

import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWordRepository @Inject constructor() {

    suspend fun saveWord(word: String, description: String): Reaction<Int> {
        return Reaction.on { 1 }
    }
}