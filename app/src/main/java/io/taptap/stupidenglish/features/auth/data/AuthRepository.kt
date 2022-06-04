package io.taptap.stupidenglish.features.auth.data

import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.sources.user.write.IWriteUserDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor (
    private val settings: Settings,
    writeUserDataSource: IWriteUserDataSource
) : IWriteUserDataSource by writeUserDataSource {

    var isFirstStart: Boolean
        get() {
            return settings.isFirstStart
        }
        set(value) {
            settings.isFirstStart = value
        }
}
