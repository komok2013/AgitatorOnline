package ru.edinros.agitator.core.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.edinros.agitator.core.data.AuthRequest
import ru.edinros.agitator.core.data.AuthResponse
import ru.edinros.agitator.core.data.PhoneRequest
import ru.edinros.agitator.core.data.PhoneResponse

interface RemoteApi {
    companion object{
        const val BASE_URL = "https://agitator-api.edinros.ru/"
    }
    @POST("/pub-api/v1.1/phone")
    suspend fun checkPhone(@Body phoneRequest: PhoneRequest): Response<PhoneResponse>

    @POST("/pub-api/v1/auth")
    suspend fun doAuth(@Body authRequest: AuthRequest): Response<AuthResponse>

}