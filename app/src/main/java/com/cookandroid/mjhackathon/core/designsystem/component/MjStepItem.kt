package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme

@Composable
fun MjStepItem(
    step: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MjHackathonTheme.colors.bgAccentSubtle, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = step.toString(),
                color = MjHackathonTheme.colors.textAccent,
                style = MjHackathonTheme.typography.captionStrong,
            )
        }

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = MjHackathonTheme.colors.textPrimary,
            style = MjHackathonTheme.typography.body1,
        )
    }
}

@Preview(name = "MjStepItem - 기본", widthDp = 360, showBackground = true)
@Composable
private fun MjStepItemPreview() {
    MjHackathonTheme {
        MjStepItem(
            step = 1,
            text = "시장에서 먹어보고 싶은 음식 찾기",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "MjStepItem - 여러 줄", widthDp = 360, showBackground = true)
@Composable
private fun MjStepItemLongTextPreview() {
    MjHackathonTheme {
        MjStepItem(
            step = 2,
            text = "시장에서 먹어보고 싶은 음식을 찾아 주문하고, 새로운 경험을 일기로 남겨 보세요.",
            modifier = Modifier.padding(16.dp),
        )
    }
}
