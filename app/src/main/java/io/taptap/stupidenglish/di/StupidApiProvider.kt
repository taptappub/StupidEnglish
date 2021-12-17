package io.taptap.stupidenglish.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class StupidApiProvider {

    @Provides
    @Singleton
    fun provideRandomWordsDataSource(): IRandomWordsDataSource {
        return RandomWordsDataSource()
    }
}