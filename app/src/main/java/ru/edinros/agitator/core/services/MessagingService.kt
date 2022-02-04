package ru.edinros.agitator.core.services

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.edinros.agitator.core.prefs.AuthPref
import ru.edinros.agitator.core.prefs.PushPref
import ru.edinros.agitator.core.repositories.AuthRepository
import timber.log.Timber
import javax.inject.Inject

@DelicateCoroutinesApi
@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var repository: AuthRepository
    override fun onNewToken(token: String) {
        Timber.d("On new token %s", token)
        PushPref.identity = token
        if (AuthPref.accessToken.isNotEmpty())
            GlobalScope.launch {
                repository.replaceToken().fold(
                    { Timber.d("upload token error") },
                    { Timber.d("upload token success") }
                )
            }
        super.onNewToken(token)
    }
}