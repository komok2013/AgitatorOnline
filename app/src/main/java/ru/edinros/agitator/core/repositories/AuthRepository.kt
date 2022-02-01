package ru.edinros.agitator.core.repositories

import arrow.core.Either
import ru.edinros.agitator.core.data.PhoneRequest
import ru.edinros.agitator.core.data.PhoneResponse
import ru.edinros.agitator.core.remote.Failure
import ru.edinros.agitator.core.remote.NetworkHandler
import ru.edinros.agitator.core.remote.RemoteApi
import ru.edinros.agitator.core.remote.safeCall
import javax.inject.Inject

class AuthRepository @Inject constructor(private val remote:RemoteApi,private val networkHandler: NetworkHandler) {
    suspend fun checkPhone(phone: String): Either<Failure, PhoneResponse> =
        when(val r = safeCall(networkHandler) { remote.checkPhone(phoneRequest = PhoneRequest(phone)) }){
            is Either.Right -> {
                if(r.value.ok){
                    r
                }else{
                    Either.Left(Failure.NoPhoneRegistered())
                }
            }
            else->r
        }
    }
