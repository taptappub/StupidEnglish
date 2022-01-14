package io.taptap.stupidenglish.features.main.data

import android.util.Log
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.mapper.toWords
import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val wordDao: WordDao,
    private val settings: Settings
) {

    var isFirstStart: Boolean
        get() {
            Log.d("MainRepository", "get isFirstStart = ${settings.isFirstStart}")
            return settings.isFirstStart
        }
        set(value) {
            Log.d("MainRepository", "set isFirstStart = $value")
            settings.isFirstStart = value
        }

    suspend fun observeWordsCount(): Reaction<Flow<List<Word>>> = Reaction.on {
        wordDao.observeWords().map { it.toWords() }
    }
}
