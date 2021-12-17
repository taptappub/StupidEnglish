package io.taptap.stupidenglish.features.addsentence.data

import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSentenceRepository @Inject constructor() {

    suspend fun saveSentence(sentence: String, wordIds: List<Int>): Reaction<Int> {
        return Reaction.on { 1 }
    }

    suspend fun getWordsById(wordsIds: List<Int>): Reaction<List<Word>> {
        return Reaction.on {
            listOf(
                Word(0, "Privet", "Ass"),
                Word(1, "Salut", "Dick"),
                Word(2, "Flibustiera", "Cunt")
            )
        }
    }
}