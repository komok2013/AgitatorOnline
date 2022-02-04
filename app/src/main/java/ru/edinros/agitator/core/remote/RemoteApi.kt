package ru.edinros.agitator.core.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import ru.edinros.agitator.core.data.*
import ru.edinros.agitator.core.prefs.AuthPref

interface RemoteApi {
    companion object{
        const val BASE_URL = "https://agitator-api.edinros.ru/"
    }
    @POST("/pub-api/v1.1/phone")
    suspend fun proceedPhoneNumber(@Body phoneRequest: PhoneRequest): Response<PhoneResponse>

    @POST("/pub-api/v1/auth")
    suspend fun proceedAuthentication(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST("pubapi/v1.1/replace-identity")
    suspend fun replaceToken(
        @Body replaceTokenRequest: ReplaceTokenRequest,
        @Header("Authorization") token: String = AuthPref.accessToken
    ): Response<ReplaceTokenResponse>

}