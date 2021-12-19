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
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseProvider {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): WordDatabase {
        return Room.databaseBuilder(
            appContext,
            WordDatabase::class.java,
            "StupidDatabase"
        ).build()
    }

    @Provides
    fun provideChannelDao(appDatabase: WordDatabase): WordDao {
        return appDatabase.wordDao()
    }
}
