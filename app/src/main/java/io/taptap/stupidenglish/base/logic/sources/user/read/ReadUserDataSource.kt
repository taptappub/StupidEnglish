package io.taptap.stupidenglish.base.logic.sources.user.read

import io.taptap.stupidenglish.base.logic.database.dao.UserDao
import io.taptap.stupidenglish.base.logic.database.dto.UserDto
import io.taptap.stupidenglish.base.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadUserDataSource @Inject constructor(
    private val userDao: UserDao
) : IReadUserDataSource {
    override suspend fun getSavedUser(): Reaction<User?> = Reaction.on {
        userDao.getUserDto().toUser()
    }

    override suspend fun observeSavedUser(): Reaction<Flow<User?>> = Reaction.on {
        userDao.observeUser()
            .map {
                it.toUser()
            }
    }
}

private fun UserDto?.toUser(): User? {
    if (this == null) return null

    return User(
        name = this.name,
        uid = this.uid,
        avatar = this.avatar
    )
}
