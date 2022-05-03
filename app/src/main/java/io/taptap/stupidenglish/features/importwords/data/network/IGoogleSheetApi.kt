package io.taptap.stupidenglish.features.importwords.data.network

import io.taptap.stupidenglish.features.importwords.data.model.GoogleTableModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IGoogleSheetApi {

    @GET("{spreadsheetId}/values/A:D")
    suspend fun getTableData(
        @Path("spreadsheetId") spreadsheetId: String,
        @Query("key") apiKey: String
    ): GoogleTableModel
}
