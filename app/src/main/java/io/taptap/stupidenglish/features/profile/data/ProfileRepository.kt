package io.taptap.stupidenglish.features.profile.data

import io.taptap.stupidenglish.base.logic.database.dao.UserDao
import io.taptap.stupidenglish.base.logic.sources.user.read.IReadUserDataSource
import io.taptap.stupidenglish.base.logic.sources.user.write.IWriteUserDataSource
import taptap.pub.Reaction
import javax.inject.Inject

class ProfileRepository @Inject constructor (
    private val userDao: UserDao,
    writeUserDataSource: IWriteUserDataSource,
    readUserDataSource: IReadUserDataSource,
) : IWriteUserDataSource by writeUserDataSource,
    IReadUserDataSource by readUserDataSource {

    suspend fun clearUser(): Reaction<Unit> = Reaction.on {
        userDao.deleteUser()
    }
}
