package ru.edinros.agitator.core.repositories

import arrow.core.Either
import ru.edinros.agitator.core.data.*
import ru.edinros.agitator.core.prefs.AuthPref
import ru.edinros.agitator.core.remote.Failure
import ru.edinros.agitator.core.remote.NetworkHandler
import ru.edinros.agitator.core.remote.RemoteApi
import ru.edinros.agitator.core.remote.safeCall
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remote: RemoteApi,
    private val networkHandler: NetworkHandler
) {
    suspend fun proceedPhoneNumber(phone: String): Either<Failure, PhoneResponse> =
        when (val r =
            safeCall(networkHandler) { remote.proceedPhoneNumber(phoneRequest = PhoneRequest(phone)) }) {
            is Either.Right -> {
                if (r.value.ok) {
                    r
                } else {
                    Either.Left(Failure.NoPhoneRegistered())
                }
            }
            else -> r
        }

    suspend fun proceedAuthentication(password: String): Either<Failure, AuthResponse> {
        val request = AuthRequest(phone = AuthPref.phone, password = password)
        return when (val r = safeCall(networkHandler) { remote.proceedAuthentication(authRequest = request) }) {
            is Either.Right -> {
                if (r.value.ok) {
                    r
                } else {
                    Either.Left(Failure.AuthenticationError())
                }
            }
            else -> r
        }
    }

    suspend fun replaceToken() = safeCall(networkHandler) {
        remote.replaceToken(ReplaceTokenRequest())
    }

}