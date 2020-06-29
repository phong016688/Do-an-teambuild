package com.phong.teamcnpm.teambuilding.utils

import android.graphics.Color
import androidx.annotation.ColorInt

object ColorUtils {
    @ColorInt
    fun calculateColor(@ColorInt to: Int, ratio: Float): Int {
        val alpha = (255 - (255 * ratio)).toInt()
        return Color.argb(alpha, Color.red(to), Color.green(to), Color.blue(to))
    }

    fun getContrastColor(color: Int): Int {
        val contrast = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(
            color)) / 255
        return if (contrast < 0.5) Color.BLACK else Color.WHITE
    }

    fun isLightColor(color: Int): Boolean {
        return getContrastColor(color) == Color.BLACK
    }
}
