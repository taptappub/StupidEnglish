package io.taptap.stupidenglish.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class StupidApiProvider {

//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext appContext: Context): WordDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            WordDatabase::class.java,
//            "StupidDatabase"
//        ).build()
//    }

//    @Provides
//    fun provideChannelDao(appDatabase: WordDatabase): WordDao {
//        return appDatabase.wordDao()
//    }

    @Provides
    @Singleton
    fun provideRandomWordsDataSource(wordDao : WordDao): IRandomWordsDataSource {
        return RandomWordsDataSource(wordDao)
    }
//
//    @Provides
//    @Singleton
//    fun provideRandomWordsDataSource(): IRandomWordsDataSource {
//        return RandomWordsDataSource()
//    }
}