package io.taptap.stupidenglish.features.splash.data

import io.taptap.stupidenglish.base.logic.prefs.Settings
import io.taptap.stupidenglish.base.logic.sources.user.read.IReadUserDataSource
import javax.inject.Inject

class SplashRepository @Inject constructor(
    private val settings: Settings,
    readUserDataSource: IReadUserDataSource,
) : IReadUserDataSource by readUserDataSource {
    var isRegistrationAsked: Boolean
        get() {
            return settings.isRegistrationAsked
        }
        set(value) {
            settings.isRegistrationAsked = value
        }

}