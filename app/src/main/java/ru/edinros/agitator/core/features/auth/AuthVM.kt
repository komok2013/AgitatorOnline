package ru.edinros.agitator.core.features.auth

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.edinros.agitator.core.prefs.AuthPref
import ru.edinros.agitator.core.repositories.AuthRepository
import javax.inject.Inject
sealed class AuthState {
    object Init : AuthState()
    object PhoneSuccess : AuthState()
    object AuthSuccess : AuthState()
    class PhoneError(val message: String) : AuthState()
    class AuthError(val message: String) : AuthState()
    object Progress : AuthState()
}
@HiltViewModel
class AuthVM @Inject constructor(private val repository: AuthRepository):ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Init)
    val state = _state.asStateFlow()

    val phoneText = mutableStateOf(AuthPref.phone)
    fun onPhoneChange(newValue: String) {
        if(newValue.length<=10 && newValue.count { it.isLetter() }==0)
            phoneText.value = newValue
    }

    fun checkPhone(phone: String) = viewModelScope.launch {
        AuthPref.phone = phone
        _state.value = AuthState.Progress
        delay(3000)
            repository.checkPhone(phone).fold(
                {
                    _state.value = AuthState.PhoneError(it.errorMsg())
                },
                {
                    //AuthPref.phone = phone
                    _state.value = AuthState.PhoneSuccess
                }
            )
        }
}