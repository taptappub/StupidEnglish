package io.taptap.stupidenglish.base.logic.sources.keys

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.taptap.stupidenglish.R
import javax.inject.Inject

class KeysDataSource @Inject constructor(
    @ApplicationContext private val appContext: Context
) : IKeysDataSource {
    override val googleApiKey: String
        get() = appContext.getString(R.string.google_sheet_key)
}
