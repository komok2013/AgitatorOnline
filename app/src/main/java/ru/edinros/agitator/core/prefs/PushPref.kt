package ru.edinros.agitator.core.prefs


import com.chibatching.kotpref.KotprefModel

object PushPref : KotprefModel() {
    var isNotify by booleanPref(default = true, commitByDefault = true)
    var identity by stringPref(default = "", commitByDefault = true)
}