package com.bestapp.zipbab.model

import androidx.annotation.ColorRes
import com.bestapp.zipbab.R

enum class UserTemperature(
    val minTemperature: Double,
    @ColorRes val colorRes: Int
) {
    LOW(0.0, R.color.temperature_min_0),
    NORMAL(30.0, R.color.temperature_min_30),
    GOOD(40.0, R.color.temperature_min_40),
    BETTER(60.0, R.color.temperature_min_60),
    EXCELLENT(80.0, R.color.temperature_min_80);

    companion object {
        fun from(temper: Double): UserTemperature {
            return when {
                temper >= EXCELLENT.minTemperature -> EXCELLENT
                temper >= BETTER.minTemperature -> BETTER
                temper >= GOOD.minTemperature -> GOOD
                temper >= NORMAL.minTemperature -> NORMAL
                else -> LOW
            }
        }
    }
}