package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme

/** 일기 작성 화면 상단에 퀘스트 제목과 장소를 표시한다. */
@Composable
fun MjQuestSummary(
    title: String,
    location: String,
    modifier: Modifier = Modifier,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colors.bgAccentSubtle, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_map_pin),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = colors.textAccent,
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.textPrimary,
                style = typography.body2Strong,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = location,
                color = colors.textSecondary,
                style = typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(name = "MjQuestSummary - 기본", widthDp = 328, showBackground = true)
@Composable
private fun MjQuestSummaryPreview() {
    MjHackathonTheme {
        MjQuestSummary(
            title = "시장에서 만나는 첫 번째 한입",
            location = "영천시장",
        )
    }
}

@Preview(name = "MjQuestSummary - 긴 텍스트", widthDp = 328, showBackground = true)
@Composable
private fun MjQuestSummaryLongTextPreview() {
    MjHackathonTheme {
        MjQuestSummary(
            title = "시장에서 만나는 첫 번째 한입과 새로운 먹거리 탐방",
            location = "서울특별시 서대문구 영천시장 먹거리 골목",
        )
    }
}
