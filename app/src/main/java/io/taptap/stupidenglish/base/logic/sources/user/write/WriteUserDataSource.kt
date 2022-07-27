package io.taptap.stupidenglish.base.logic.sources.user.write

import io.taptap.stupidenglish.base.logic.database.dao.UserDao
import io.taptap.stupidenglish.base.logic.database.dto.UserDto
import taptap.pub.Reaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WriteUserDataSource @Inject constructor(
    private val userDao: UserDao
) : IWriteUserDataSource {

    override suspend fun saveUser(
        name: String,
        image: String,
        email: String,
        emailVerified: Boolean?,
        phoneNumber: String?,
        uid: String
    ): Reaction<Unit> = Reaction.on {
        userDao.insertUser(
            UserDto(
                name = name,
                email = email,
                avatar = image,
                emailVerified = emailVerified,
                phoneNumber = phoneNumber,
                uid = uid
            )
        )
    }

}
