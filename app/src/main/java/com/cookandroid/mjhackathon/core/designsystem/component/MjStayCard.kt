package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme

data class MjStayCardData(
    val daysRemaining: Int = 115,
    val startDate: String = "2026.09.01",
    val endDate: String = "2027.02.28",
    val progress: Float = 0.36f,
)

/** 목업 값을 그대로 표시하며 현재 날짜 계산이나 서버 요청은 수행하지 않는다. */
@Composable
fun MjStayCard(
    modifier: Modifier = Modifier,
    data: MjStayCardData = MjStayCardData(),
) {
    val colors = MjHackathonTheme.colors
    val progress = if (data.progress.isFinite()) data.progress.coerceIn(0f, 1f) else 0f
    val dDay = when {
        data.daysRemaining > 0 -> "D-${data.daysRemaining}"
        data.daysRemaining == 0 -> "D-Day"
        else -> "D+${-data.daysRemaining.toLong()}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = colors.bgInverse.copy(alpha = 0.08f),
                spotColor = colors.bgInverse.copy(alpha = 0.08f),
            )
            .clip(RoundedCornerShape(20.dp))
            .background(colors.bgSurface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = dDay,
            color = colors.textPrimary,
            style = MjHackathonTheme.typography.display,
        )

        Text(
            text = "${data.startDate} – ${data.endDate}",
            color = colors.textSecondary,
            style = MjHackathonTheme.typography.body2,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(colors.bgMuted)
                .semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f)
                },
        ) {
            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colors.bgInverse),
                )
            }
        }
    }
}

@Preview(name = "MjStayCard - 목업", widthDp = 344, showBackground = true, backgroundColor = 0xFFF6F5F2)
@Composable
private fun MjStayCardPreview() {
    MjHackathonTheme {
        MjStayCard(modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "MjStayCard - 시작", widthDp = 344, showBackground = true, backgroundColor = 0xFFF6F5F2)
@Composable
private fun MjStayCardStartPreview() {
    MjHackathonTheme {
        MjStayCard(
            data = MjStayCardData(daysRemaining = 180, progress = 0f),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "MjStayCard - 귀국 당일", widthDp = 344, showBackground = true, backgroundColor = 0xFFF6F5F2)
@Composable
private fun MjStayCardReturnPreview() {
    MjHackathonTheme {
        MjStayCard(
            data = MjStayCardData(daysRemaining = 0, progress = 1f),
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(name = "MjStayCard - 귀국 이후", widthDp = 344, showBackground = true, backgroundColor = 0xFFF6F5F2)
@Composable
private fun MjStayCardAfterReturnPreview() {
    MjHackathonTheme {
        MjStayCard(
            data = MjStayCardData(daysRemaining = -1, progress = 1f),
            modifier = Modifier.padding(20.dp),
        )
    }
}
