package io.taptap.stupidenglish.base.logic.sources.user.write

import taptap.pub.Reaction

interface IWriteUserDataSource {

    suspend fun saveUser(
        name: String,
        image: String,
        email: String,
        emailVerified: Boolean?,
        phoneNumber: String?,
        uid: String
    ): Reaction<Unit>
}
