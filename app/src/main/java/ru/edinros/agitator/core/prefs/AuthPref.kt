package ru.edinros.agitator.core.prefs


import com.chibatching.kotpref.KotprefModel

object AuthPref : KotprefModel() {
    var phone by stringPref(default = "", commitByDefault = true)
    var accessToken by stringPref(default = "", commitByDefault = true)
}