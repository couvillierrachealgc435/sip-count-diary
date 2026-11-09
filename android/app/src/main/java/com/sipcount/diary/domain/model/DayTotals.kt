package com.sipcount.diary.domain.model

data class DayTotals(
    val epochDay: Long,
    val totalMl: Int,
    val perType: Map<String, Int>,
    val count: Int
) {
    val isEmpty: Boolean get() = count == 0
}
