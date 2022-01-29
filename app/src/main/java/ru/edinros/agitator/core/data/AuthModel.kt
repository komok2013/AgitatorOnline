package ru.edinros.agitator.core.data

import kotlinx.serialization.Serializable
import ru.edinros.agitator.core.prefs.PushPref

@Serializable
data class PhoneRequest(
    val phone: String
)
@Serializable
data class PhoneResponse(
    val ok: Boolean
)
@Serializable
data class AuthRequest(
    val phone: String,
    val password: String,
    val provider: String = "fcm",
    val identity: String = PushPref.identity
)
@Serializable
data class AuthResponse(
    val ok: Boolean,
    val token: String,
    val is_interviewer: Boolean,
    val is_team_leader: Boolean
)

@Serializable
data class ReplaceTokenRequest(
    val identity: String = PushPref.identity
)
@Serializable
data class ReplaceTokenResponse(
    val ok: Boolean = false
)
