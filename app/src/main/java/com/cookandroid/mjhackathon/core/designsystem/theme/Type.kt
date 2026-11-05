package com.cookandroid.mjhackathon.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.cookandroid.mjhackathon.R

@Immutable
data class MjHackathonTypography(
    val display: TextStyle,
    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val body1: TextStyle,
    val body1Strong: TextStyle,
    val body2: TextStyle,
    val body2Strong: TextStyle,
    val buttonL: TextStyle,
    val buttonM: TextStyle,
    val caption: TextStyle,
    val captionStrong: TextStyle,
)

private fun typographyStyle(
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight,
    letterSpacing: Float = 0f,
): TextStyle = TextStyle(
    fontFamily = NotoSansFontFamily,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    fontWeight = fontWeight,
    letterSpacing = letterSpacing.sp,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false,
    ),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

private val NotoSansFontFamily = FontFamily(
    Font(R.font.notosans_regular, weight = FontWeight.Normal),
    Font(R.font.notosans_medium, weight = FontWeight.Medium),
    Font(R.font.notosans_bold, weight = FontWeight.Bold),
)

val defaultMjHackathonTypography = MjHackathonTypography(
    display = typographyStyle(
        fontSize = 36,
        lineHeight = 44,
        fontWeight = FontWeight.Bold,
    ),
    title1 = typographyStyle(
        fontSize = 24,
        lineHeight = 32,
        fontWeight = FontWeight.Bold,
    ),
    title2 = typographyStyle(
        fontSize = 20,
        lineHeight = 28,
        fontWeight = FontWeight.Bold,
    ),
    title3 = typographyStyle(
        fontSize = 18,
        lineHeight = 26,
        fontWeight = FontWeight.Bold,
    ),
    body1 = typographyStyle(
        fontSize = 16,
        lineHeight = 24,
        fontWeight = FontWeight.Normal,
    ),
    body1Strong = typographyStyle(
        fontSize = 16,
        lineHeight = 24,
        fontWeight = FontWeight.Medium,
    ),
    body2 = typographyStyle(
        fontSize = 14,
        lineHeight = 20,
        fontWeight = FontWeight.Normal,
    ),
    body2Strong = typographyStyle(
        fontSize = 14,
        lineHeight = 20,
        fontWeight = FontWeight.Medium,
    ),
    buttonL = typographyStyle(
        fontSize = 16,
        lineHeight = 24,
        fontWeight = FontWeight.Bold,
    ),
    buttonM = typographyStyle(
        fontSize = 14,
        lineHeight = 20,
        fontWeight = FontWeight.Bold,
    ),
    caption = typographyStyle(
        fontSize = 12,
        lineHeight = 16,
        fontWeight = FontWeight.Normal,
    ),
    captionStrong = typographyStyle(
        fontSize = 12,
        lineHeight = 16,
        fontWeight = FontWeight.Medium,
    ),
)

val LocalMjHackathonTypographyProvider = staticCompositionLocalOf {
    defaultMjHackathonTypography
}
