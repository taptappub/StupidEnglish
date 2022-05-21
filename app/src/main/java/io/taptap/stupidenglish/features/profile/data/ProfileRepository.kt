package io.taptap.stupidenglish.features.profile.data

import io.taptap.stupidenglish.base.logic.sources.user.read.IReadUserDataSource
import io.taptap.stupidenglish.base.logic.sources.user.write.IWriteUserDataSource
import javax.inject.Inject

class ProfileRepository @Inject constructor (
    writeUserDataSource: IWriteUserDataSource,
    readUserDataSource: IReadUserDataSource,
) : IWriteUserDataSource by writeUserDataSource,
    IReadUserDataSource by readUserDataSource