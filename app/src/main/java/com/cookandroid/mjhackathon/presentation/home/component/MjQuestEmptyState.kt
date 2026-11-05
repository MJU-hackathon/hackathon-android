package com.cookandroid.mjhackathon.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme

@Composable
fun MjQuestEmptyState(modifier: Modifier = Modifier) {
    val colors = MjHackathonTheme.colors
    Column(
        modifier = modifier.fillMaxWidth()
            .background(colors.bgSurface, RoundedCornerShape(24.dp))
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(64.dp).background(colors.bgAccentSubtle, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_compass),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = colors.textAccent,
            )
        }
        Text(
            text = "오늘 진행할 퀘스트가 없어요!",
            style = MjHackathonTheme.typography.body1Strong,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "한국 시간 자정이 지나면 새 퀘스트\n3개가 준비돼요",
            style = MjHackathonTheme.typography.body2,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "퀘스트 - 오늘 모두 처리", widthDp = 320, showBackground = true)
@Composable
private fun MjQuestEmptyStatePreview() {
    MjHackathonTheme { MjQuestEmptyState() }
}
