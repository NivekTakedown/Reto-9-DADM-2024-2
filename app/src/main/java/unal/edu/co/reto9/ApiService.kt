package unal.edu.co.reto9

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/interpreter")
    suspend fun getPOIs(
        @Query("data") query: String
    ): Response<OverpassResponse>
}