package com.example.hywater.data.remote.api

import com.example.hywater.data.remote.dto.FountainDto
import com.example.hywater.data.remote.dto.FountainsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FountainApiService {

    /**
     * Fetch all approved fountains, optionally filtered by bounding box.
     * The backend should only return approved fountains on this endpoint;
     * pending fountains are visible to their author via the /my endpoint.
     */
    @GET("fountains")
    suspend fun getFountains(
        @Query("lat") latitude: Double? = null,
        @Query("lng") longitude: Double? = null,
        @Query("radius_km") radiusKm: Double? = null
    ): FountainsResponse

    /**
     * Fetch a single fountain by ID (approved or pending — for detail view).
     */
    @GET("fountains/{id}")
    suspend fun getFountainById(@Path("id") id: Long): FountainDto

    /**
     * Submit a new fountain.
     * Uses multipart/form-data so the photo binary is uploaded alongside metadata.
     *
     * Required parts:
     *  - photo:       the JPEG binary
     *  - latitude:    plain text
     *  - longitude:   plain text
     *  - description: plain text
     */
    @Multipart
    @POST("fountains")
    suspend fun createFountain(
        @Part photo: MultipartBody.Part,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("description") description: RequestBody
    ): FountainDto
}
