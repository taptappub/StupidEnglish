package io.taptap.stupidenglish.features.importwords.data.network

import io.taptap.stupidenglish.features.importwords.data.model.GoogleImportModel
import retrofit2.http.*

interface IGoogleSheetApi {

    @GET("{spreadsheetId}/values/A:D")
    suspend fun getTableData(
        @Path("spreadsheetId") spreadsheetId: String,
        @Query("key") apiKey: String
    ): List<GoogleImportModel>
}
