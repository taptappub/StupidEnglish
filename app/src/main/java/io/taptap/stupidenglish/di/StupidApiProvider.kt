package io.taptap.stupidenglish.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.logic.share.ShareUtil
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class StupidApiProvider {

    @Provides
    @Singleton
    fun provideRandomWordsDataSource(wordDao : WordDao): IRandomWordsDataSource {
        return RandomWordsDataSource(wordDao)
    }

    @Provides
    @Singleton
    fun provideShareUtil(@ApplicationContext appContext: Context): ShareUtil {
        return ShareUtil(appContext)
    }
}