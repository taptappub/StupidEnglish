package io.taptap.stupidenglish.features.auth.data

import io.taptap.stupidenglish.base.logic.sources.user.write.IWriteUserDataSource
import javax.inject.Inject

class AuthRepository @Inject constructor (
    writeUserDataSource: IWriteUserDataSource
) : IWriteUserDataSource by writeUserDataSource {
}