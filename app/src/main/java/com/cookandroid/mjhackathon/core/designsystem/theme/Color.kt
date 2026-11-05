package com.cookandroid.mjhackathon.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val Gray300 = Color(0xFF393939)
private val Gray200 = Color(0xFFB6B6B6)
private val Gray100 = Color(0xFFF5F5F5)

private val Blue300 = Color(0xFF2F363E)
private val Blue200 = Color(0xFF4A535A)
private val Blue100 = Color(0xFFB8BCBF)

private val White = Color(0xFFFFFFFF)
private val Black = Color(0xFF191919)

private val Yellow400 = Color(0xFFFFF9C4)
private val Yellow500 = Color(0xFFFEE500)
private val Yellow600 = Color(0xFFF6DE00)

private val Orange300 = Color(0xFFEA5B21)
private val Orange200 = Color(0xFFFF9117)
private val Orange100 = Color(0xFFFDEEE7)

@Immutable
data class MjHackathonColors(
    val gray300: Color,
    val gray200: Color,
    val gray100: Color,

    val blue300: Color,
    val blue200: Color,
    val blue100: Color,

    val white: Color,
    val black: Color,

    val yellow400: Color,
    val yellow500: Color,
    val yellow600: Color,

    val orange300: Color,
    val orange200: Color,
    val orange100: Color
)

val defaultMjHackathonColors = MjHackathonColors(
    gray300 = Gray300,
    gray200 = Gray200,
    gray100 = Gray100,

    blue300 = Blue300,
    blue200 = Blue200,
    blue100 = Blue100,

    white = White,
    black = Black,

    yellow400 = Yellow400,
    yellow500 = Yellow500,
    yellow600 = Yellow600,

    orange300 = Orange300,
    orange200 = Orange200,
    orange100 = Orange100
)

val LocalMjHackathonColorProvider = staticCompositionLocalOf {
    defaultMjHackathonColors
}
