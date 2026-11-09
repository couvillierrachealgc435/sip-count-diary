package com.sipcount.diary.core.util

import com.sipcount.diary.R

object Sprites {

    private val ids = intArrayOf(
        R.drawable.sprite_1,
        R.drawable.sprite_2,
        R.drawable.sprite_3,
        R.drawable.sprite_4,
        R.drawable.sprite_5
    )

    val count: Int get() = ids.size

    fun at(index: Int): Int = ids[index.coerceIn(0, ids.lastIndex)]
}
