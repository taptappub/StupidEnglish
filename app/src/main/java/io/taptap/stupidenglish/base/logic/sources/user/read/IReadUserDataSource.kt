package io.taptap.stupidenglish.base.logic.sources.user.read

import io.taptap.stupidenglish.base.model.User
import taptap.pub.Reaction

interface IReadUserDataSource {

    suspend fun getSavedUser(): Reaction<User?>
}