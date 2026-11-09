package com.sipcount.diary.core.util

import java.time.LocalDate
import java.time.LocalTime

object Today {

    fun epochDay(): Long = LocalDate.now().toEpochDay()

    fun minuteOfDay(): Int {
        val now = LocalTime.now()
        return now.hour * 60 + now.minute
    }

    fun clock(minuteOfDay: Int): String {
        val hours = (minuteOfDay / 60).coerceIn(0, 23)
        val minutes = (minuteOfDay % 60).coerceIn(0, 59)
        return hours.toString().padStart(2, '0') + ":" + minutes.toString().padStart(2, '0')
    }

    fun weekdayIndex(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).dayOfWeek.value - 1

    fun dayOfMonth(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).dayOfMonth

    fun monthIndex(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).monthValue - 1

    fun year(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).year

    fun firstOfMonth(epochDay: Long): Long =
        LocalDate.ofEpochDay(epochDay).withDayOfMonth(1).toEpochDay()

    fun lengthOfMonth(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).lengthOfMonth()

    fun shiftMonths(epochDay: Long, months: Long): Long =
        LocalDate.ofEpochDay(epochDay).withDayOfMonth(1).plusMonths(months).toEpochDay()
}
