package io.taptap.stupidenglish.base.logic.sources.user.read

import io.taptap.stupidenglish.base.model.User
import taptap.pub.Reaction
import kotlinx.coroutines.flow.Flow

interface IReadUserDataSource {

    suspend fun getSavedUser(): Reaction<User?>
    suspend fun observeSavedUser(): Reaction<Flow<User?>>
}