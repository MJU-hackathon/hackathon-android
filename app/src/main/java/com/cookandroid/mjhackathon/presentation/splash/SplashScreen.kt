package com.cookandroid.mjhackathon.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme

@Composable
fun SplashRoute(
    modifier: Modifier = Modifier,
) {
    SplashScreen(modifier = modifier)
}

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography

    Box(
        modifier = modifier.fillMaxSize().background(colors.bgAccent),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_klog),
                    contentDescription = null,
                    modifier = Modifier.size(width = 144.dp, height = 184.dp),
                    contentScale = ContentScale.Fit,
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "K-log",
                    color = colors.bgSurface,
                    style = typography.display.copy(
                        fontSize = 48.sp,
                        lineHeight = 56.sp,
                        shadow = Shadow(
                            color = colors.bgInverse.copy(alpha = 0.12f),
                            offset = Offset(0f, 3f),
                            blurRadius = 4f,
                        ),
                    ),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "내 동네의 K-콘텐츠, 나만의 유학 일기로",
                    color = colors.bgSurface,
                    style = typography.body1Strong,
                    textAlign = TextAlign.Center,
                )
            }

            Text(
                text = "Korea · Keep · Log",
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                color = colors.textAccent,
                style = typography.caption,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(name = "Splash", widthDp = 360, heightDp = 800, showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    MjHackathonTheme { SplashScreen() }
}

@Preview(name = "Splash - 작은 화면", widthDp = 320, heightDp = 640, showBackground = true)
@Composable
private fun SplashScreenCompactPreview() {
    MjHackathonTheme { SplashScreen() }
}
