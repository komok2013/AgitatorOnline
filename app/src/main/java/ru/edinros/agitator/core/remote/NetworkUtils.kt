package ru.edinros.agitator.core.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import arrow.core.Either
import retrofit2.Response

class NetworkHandler(private val context: Context) {
    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}

suspend fun <T> safeCall(networkHandler: NetworkHandler, block: suspend () -> Response<T>) = try {
    when (networkHandler.isNetworkAvailable()) {
        true -> {
            val res = block()
            when {
                res.isSuccessful -> {
                    when (val body = res.body()) {
                        null -> {
                            Either.Left(Failure.ResponseBodyIsNull)
                        }
                        else -> {
                            Either.Right(body)
                        }
                    }
                }
                else -> {
                    Either.Left(
                        when (res.code()) {
                            401 -> Failure.HttpErrorUnauthorized()
                            else -> Failure.HttpErrorOther(error = "код ошибки ${res.code()}")
                        }
                    )
                }
            }
        }
        else -> Either.Left(Failure.NoNetworkConnection)
    }
} catch (e: Exception) {
    Either.Left(Failure.GenericError(exception = e))
}

sealed class Failure {
    object NoNetworkConnection : Failure()
    object ResponseBodyIsNull:Failure()
    class HttpErrorUnauthorized(val error: String = "неавторизованный доступ") : Failure()
    class HttpErrorOther(val error: String = "") : Failure()
    class GenericError(val error: String = "Что-то пошло не так", val exception: Throwable) :        Failure()

    class NoPhoneRegistered(val error: String = "данный номер не зарегистрирован в системе") :
        Failure()

    class NoValidPhone(val error: String = "не валидный номер телефона") : Failure()
    class AuthenticationError(val error: String = "ошибка аутентификации") : Failure()
    class NoTask (val error: String="Ошибка при выдаче задач"):Failure()

    fun errorMsg() = when (this) {
        NoNetworkConnection -> "нет сетевого соединения"
        is AuthenticationError -> error
        is GenericError -> error
        is HttpErrorOther -> error
        is HttpErrorUnauthorized -> error
        is NoPhoneRegistered -> error
        is NoValidPhone -> error
        ResponseBodyIsNull -> "пустой ответ"
        is NoTask -> error
    }

    fun exceptionMsg() = when (this) {
        is GenericError -> exception.message ?: ""
        else -> ""
    }
}
