package ru.edinros.agitator

import timber.log.Timber
import javax.inject.Inject

class MainRepository @Inject constructor() {
    fun test()= Timber.d("repository is %s",this)
}