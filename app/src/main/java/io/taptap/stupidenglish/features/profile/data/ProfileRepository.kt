package io.taptap.stupidenglish.features.profile.data

import io.taptap.stupidenglish.base.logic.database.dao.UserDao
import io.taptap.stupidenglish.base.logic.sources.user.read.IReadUserDataSource
import io.taptap.stupidenglish.base.logic.sources.user.write.IWriteUserDataSource
import io.taptap.uikit.prefs.ThemeType
import io.taptap.uikit.prefs.UiKitSettings
import taptap.pub.Reaction
import javax.inject.Inject

class ProfileRepository @Inject constructor (
    private val userDao: UserDao,
    private val uiKitSettings: UiKitSettings,
    writeUserDataSource: IWriteUserDataSource,
    readUserDataSource: IReadUserDataSource,
) : IWriteUserDataSource by writeUserDataSource,
    IReadUserDataSource by readUserDataSource {

    var theme: ThemeType
        get() {
            return ThemeType.getByValue(uiKitSettings.theme)
        }
        set(value) {
            uiKitSettings.theme = value.value
        }

    suspend fun clearUser(): Reaction<Unit> = Reaction.on {
        userDao.deleteUser()
    }
}
