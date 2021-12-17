package io.taptap.stupidenglish.features.main.data

import io.taptap.stupidenglish.base.model.Word
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainListRepository @Inject constructor() {

    suspend fun getWordList(): Reaction<List<Word>> {
        return Reaction.on {
            listOf(
                Word(0, "Privet", "Ass"),
                Word(1, "Salut", "Dick"),
                Word(2, "Flibustiera", "Cunt")
            )
        }
    }
}