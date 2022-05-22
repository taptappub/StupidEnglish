package io.taptap.stupidenglish.di

import android.app.AlarmManager
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.share.ShareUtil
import io.taptap.stupidenglish.features.addsentence.navigation.AddSentenceArgumentsMapper
import io.taptap.stupidenglish.features.alarm.ui.AlarmScheduler
import io.taptap.uikit.prefs.UiKitSettings
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@ExperimentalMaterialNavigationApi
@InternalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@InstallIn(SingletonComponent::class)
@Module
class StupidApiProvider {

    @Provides
    @Singleton
    fun provideShareUtil(@ApplicationContext appContext: Context): ShareUtil {
        return ShareUtil(appContext)
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext appContext: Context): ImageLoader {
        return ImageLoader.Builder(appContext)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext appContext: Context): Settings {
        return Settings(appContext)
    }

    @Provides
    @Singleton
    fun provideUiKitSettings(@ApplicationContext appContext: Context): UiKitSettings {
        return UiKitSettings(appContext)
    }

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext appContext: Context): AlarmManager {
        return appContext.getSystemService(ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        alarmManager: AlarmManager,
        @ApplicationContext appContext: Context
    ): AlarmScheduler {
        return AlarmScheduler(alarmManager, appContext)
    }

    @Provides
    @Singleton
    fun provideAddSentenceArgumentsMapper(): AddSentenceArgumentsMapper {
        return AddSentenceArgumentsMapper
    }

}