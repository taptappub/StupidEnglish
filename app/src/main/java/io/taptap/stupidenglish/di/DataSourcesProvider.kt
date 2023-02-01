package io.taptap.stupidenglish.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.taptap.stupidenglish.base.logic.database.dao.UserDao
import io.taptap.stupidenglish.base.logic.database.dao.WordDao
import io.taptap.stupidenglish.base.logic.randomwords.IRandomWordsDataSource
import io.taptap.stupidenglish.base.logic.randomwords.RandomWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.read.IReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.read.ReadGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.IWriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.groups.write.WriteGroupsDataSource
import io.taptap.stupidenglish.base.logic.sources.keys.IKeysDataSource
import io.taptap.stupidenglish.base.logic.sources.keys.KeysDataSource
import io.taptap.stupidenglish.base.logic.sources.user.read.IReadUserDataSource
import io.taptap.stupidenglish.base.logic.sources.user.read.ReadUserDataSource
import io.taptap.stupidenglish.base.logic.sources.user.write.IWriteUserDataSource
import io.taptap.stupidenglish.base.logic.sources.user.write.WriteUserDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.IReadWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.read.ReadWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.IWriteWordsDataSource
import io.taptap.stupidenglish.base.logic.sources.words.write.WriteWordsDataSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataSourcesProvider {

    @Provides
    @Singleton
    fun provideRandomWordsDataSource(readWordsDataSource: IReadWordsDataSource): IRandomWordsDataSource =
        RandomWordsDataSource(readWordsDataSource)

    @Provides
    @Singleton
    fun provideWriteUserDataSource(userDao: UserDao): IWriteUserDataSource =
        WriteUserDataSource(userDao)

    @Provides
    @Singleton
    fun provideReadUserDataSource(userDao: UserDao): IReadUserDataSource =
        ReadUserDataSource(userDao)

    @Provides
    @Singleton
    fun provideKeysDataSource(@ApplicationContext appContext: Context): IKeysDataSource =
        KeysDataSource(appContext)

    @Provides
    @Singleton
    fun provideWriteGroupsDataSource(wordDao : WordDao): IWriteGroupsDataSource =
        WriteGroupsDataSource(wordDao)

    @Provides
    @Singleton
    fun provideReadGroupsDataSource(wordDao : WordDao): IReadGroupsDataSource =
        ReadGroupsDataSource(wordDao)

    @Provides
    @Singleton
    fun provideWordsDataSource(wordDao : WordDao): IWriteWordsDataSource =
        WriteWordsDataSource(wordDao)

    @Provides
    @Singleton
    fun provideReadWordsDataSource(wordDao : WordDao): IReadWordsDataSource =
        ReadWordsDataSource(wordDao)
}