package io.taptap.stupidenglish.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.taptap.stupidenglish.base.logic.database.WordDatabase
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.database.dto.SentenceDto
import io.taptap.stupidenglish.base.logic.database.dto.WordDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseProvider {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): WordDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            WordDatabase::class.java,
//            "StupidDatabase"
//        ).build()
        val db = Room.databaseBuilder(
            appContext,
            WordDatabase::class.java,
            "StupidDatabase"
        ).build()
//        db.fillApp()
        return db
    }

    @Provides
    @Singleton
    fun provideChannelDao(appDatabase: WordDatabase): WordDao {
        return appDatabase.wordDao()
    }
}

private fun WordDatabase.fillApp() {
    val dao = this.wordDao()
    GlobalScope.launch(Dispatchers.IO) {
        val wordId = dao.insertWord(WordDto(word = "Priv", description = "Hello"))
        val wordId1 = dao.insertWord(WordDto(word = "Priv1", description = "Hello"))
        val wordId2 = dao.insertWord(WordDto(word = "Priv2", description = "Hello"))
        val wordId3 = dao.insertWord(WordDto(word = "Priv3", description = "Hello"))

        val sentenceId =
            dao.insertSentence(SentenceDto(sentence = "sentence Priv", words = "{1,2,3}}"))
        val sentenceId1 =
            dao.insertSentence(SentenceDto(sentence = "sentence Priv1", words = "{1,2,3}}"))
        val sentenceId2 =
            dao.insertSentence(SentenceDto(sentence = "sentence Priv2", words = "{1,2,3}}"))
        val sentenceId3 =
            dao.insertSentence(SentenceDto(sentence = "sentence Priv3", words = "{1,2,3}}"))
    }
}
