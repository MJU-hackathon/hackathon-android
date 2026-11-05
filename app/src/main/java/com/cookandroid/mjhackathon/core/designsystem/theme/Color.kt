package com.cookandroid.mjhackathon.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val Green50 = Color(0xFFFFF8E5)
private val Green100 = Color(0xFFFFEFC0)
private val Green200 = Color(0xFFFFE59A)
private val Green300 = Color(0xFFFFDA73)
private val Green400 = Color(0xFFFFD15A)
private val Green500 = Color(0xFFFFCB4B)
private val Green600 = Color(0xFFFFC53D)
private val Green700 = Color(0xFF9A6B00)
private val Green800 = Color(0xFF7A5400)
private val Green900 = Color(0xFF573C00)

private val Yellow50 = Color(0xFFFFF8E1)
private val Yellow100 = Color(0xFFFFEDB3)
private val Yellow200 = Color(0xFFFFD666)
private val Yellow400 = Color(0xFFFFC53D)
private val Yellow500 = Color(0xFFF5A623)
private val Yellow700 = Color(0xFF9A6200)

private val Gray0 = Color(0xFFFFFFFF)
private val Gray25 = Color(0xFFFBFAF8)
private val Gray50 = Color(0xFFF6F5F2)
private val Gray100 = Color(0xFFEEECE7)
private val Gray200 = Color(0xFFE2DFD8)
private val Gray300 = Color(0xFFCBC7BE)
private val Gray400 = Color(0xFFA8A398)
private val Gray500 = Color(0xFF858074)
private val Gray600 = Color(0xFF645F55)
private val Gray700 = Color(0xFF47433C)
private val Gray800 = Color(0xFF2E2B27)
private val Gray900 = Color(0xFF1C1A17)

private val Red50 = Color(0xFFFDECEC)
private val Red500 = Color(0xFFE5484D)
private val Red600 = Color(0xFFC93A3F)

@Immutable
data class MjHackathonColors(
    // Background
    val bgCanvas: Color,
    val bgSurface: Color,
    val bgMuted: Color,
    val bgBrandSubtle: Color,
    val bgAccentSubtle: Color,
    val bgAccent: Color,
    val bgInverse: Color,

    // Action
    val actionPrimary: Color,
    val actionPrimaryPressed: Color,
    val actionSecondary: Color,
    val actionDisabled: Color,

    // Status
    val statusDidBg: Color,
    val statusMissedBg: Color,

    // Feedback
    val feedbackErrorBg: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val textOnBrand: Color,
    val textBrand: Color,
    val textAccent: Color,
    val textDid: Color,
    val textMissed: Color,
    val textError: Color,

    // Border
    val borderDefault: Color,
    val borderStrong: Color,
    val borderBrand: Color,
    val borderError: Color,

    // Icon
    val iconDefault: Color,
    val iconSecondary: Color,
    val iconBrand: Color,
    val iconOnBrand: Color,
    val iconAccent: Color,
)

val defaultMjHackathonColors = MjHackathonColors(
    // Background
    bgCanvas = Gray50,
    bgSurface = Gray0,
    bgMuted = Gray100,
    bgBrandSubtle = Green50,
    bgAccentSubtle = Yellow50,
    bgAccent = Yellow400,
    bgInverse = Gray900,

    // Action
    actionPrimary = Green600,
    actionPrimaryPressed = Yellow500,
    actionSecondary = Gray0,
    actionDisabled = Gray200,

    // Status
    statusDidBg = Green50,
    statusMissedBg = Gray100,

    // Feedback
    feedbackErrorBg = Red50,

    // Text
    textPrimary = Gray900,
    textSecondary = Gray600,
    textTertiary = Gray500,
    textDisabled = Gray400,
    textOnBrand = Gray900,
    textBrand = Green700,
    textAccent = Yellow700,
    textDid = Green700,
    textMissed = Gray700,
    textError = Red600,

    // Border
    borderDefault = Gray200,
    borderStrong = Gray900,
    borderBrand = Green700,
    borderError = Red500,

    // Icon
    iconDefault = Gray900,
    iconSecondary = Gray500,
    iconBrand = Green700,
    iconOnBrand = Gray900,
    iconAccent = Yellow500,
)

val LocalMjHackathonColorProvider = staticCompositionLocalOf {
    defaultMjHackathonColors
}
