package ru.edinros.agitator.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.edinros.agitator.core.prefs.AuthPref
import ru.edinros.agitator.core.repositories.AuthRepository
import javax.inject.Inject
sealed class AuthState {
    object Init : AuthState()
    object Progress : AuthState()
    object PhoneSuccess : AuthState()
    class PhoneError(val message: String) : AuthState()
    object AuthSuccess : AuthState()
    class AuthError(val message: String) : AuthState()
}
@HiltViewModel
class AuthVM @Inject constructor(private val repository: AuthRepository):ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Init)
    val state = _state.asStateFlow()

    fun proceedPhoneNumber(phone: String) = viewModelScope.launch {
        AuthPref.phone = phone
        _state.value = AuthState.Progress
            repository.proceedPhoneNumber(phone).fold(
                {
                    _state.value = AuthState.PhoneError(it.errorMsg())
                },
                {
                    _state.value = AuthState.PhoneSuccess
                }
            )
        }

    fun proceedAuthentication(code: String)=viewModelScope.launch {
        _state.value = AuthState.Progress
        repository.proceedAuthentication(code).fold(
            {
                _state.value = AuthState.AuthError(it.errorMsg())
            },
            {
                AuthPref.accessToken = String.format("Token %s", it.token)
                _state.value = AuthState.AuthSuccess
            }
        )
    }
}