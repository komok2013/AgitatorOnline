package ru.edinros.agitator.core.utils

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
fun currentTimeInSec() = (System.currentTimeMillis() / 1000).toInt()

fun formatterWithDay(seconds: Int?): String =
    when {
        seconds != null -> DateTimeFormatter.ofPattern("dd-MM-yyyy").format(
            LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds.toLong()), ZoneId.systemDefault())
        )
        else -> ""
    }

fun formatterWithDayTime(seconds: Int): String =
    DateTimeFormatter.ofPattern("dd-MM-yyyy, kk:mm:ss").format(
        LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds.toLong()), ZoneId.systemDefault())
    )

fun formatterWithDayTimeSlash(seconds: Int): String =
    DateTimeFormatter.ofPattern("dd-MM-yyyy\nkk:mm:ss").format(
        LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds.toLong()), ZoneId.systemDefault())
    )