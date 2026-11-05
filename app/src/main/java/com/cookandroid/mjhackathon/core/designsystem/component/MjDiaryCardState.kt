package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import coil3.compose.SubcomposeAsyncImage

enum class MjDiaryCardState {
    Did,
    Missed,
}

/** 보기 전용 일기 카드. imageUri는 촬영 사진 URI 또는 서버 이미지 URL을 받는다. */
@Composable
fun MjDiaryCard(
    state: MjDiaryCardState,
    date: String,
    location: String,
    title: String,
    memo: String,
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int? = null,
    imageUri: String? = null,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape)
            .clip(shape)
            .background(colors.bgSurface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
                .background(colors.bgMuted),
            contentAlignment = Alignment.Center,
        ) {
            if (!imageUri.isNullOrBlank() && !LocalInspectionMode.current) {
                SubcomposeAsyncImage(
                    model = imageUri,
                    contentDescription = "일기에 남긴 경험 사진",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = { DiaryPhotoPlaceholder() },
                    error = { DiaryPhotoPlaceholder() },
                )
            } else if (imageRes == null) {
                DiaryPhotoPlaceholder()
            } else {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$date · $location",
                    modifier = Modifier.weight(1f),
                    color = colors.textSecondary,
                    style = typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                MjDiaryCardBadge(state)
            }

            Text(
                text = title,
                color = colors.textPrimary,
                style = typography.body1Strong,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (memo.isNotEmpty()) {
                Text(
                    text = memo,
                    color = colors.textSecondary,
                    style = typography.body2,
                )
            }
        }
    }
}

@Composable
private fun DiaryPhotoPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.ic_gallery),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MjHackathonTheme.colors.iconSecondary,
        )
    }
}

@Composable
private fun MjDiaryCardBadge(state: MjDiaryCardState) {
    val colors = MjHackathonTheme.colors
    val backgroundColor = when (state) {
        MjDiaryCardState.Did -> colors.statusDidBg
        MjDiaryCardState.Missed -> colors.statusMissedBg
    }
    val textColor = when (state) {
        MjDiaryCardState.Did -> colors.textDid
        MjDiaryCardState.Missed -> colors.textMissed
    }

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state == MjDiaryCardState.Did) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = textColor,
            )
        }
        Text(
            text = when (state) {
                MjDiaryCardState.Did -> "해봤어요!"
                MjDiaryCardState.Missed -> "이번엔 못 했어요"
            },
            color = textColor,
            style = MjHackathonTheme.typography.captionStrong,
        )
    }
}

@Preview(name = "MjDiaryCard - 해봤어요", widthDp = 360, showBackground = true)
@Composable
private fun MjDiaryCardDidPreview() {
    MjHackathonTheme {
        Box(Modifier.background(MjHackathonTheme.colors.bgCanvas).padding(20.dp)) {
            MjDiaryCard(
                state = MjDiaryCardState.Did,
                date = "2026.11.05",
                location = "영천시장",
                title = "시장에서 만나는 첫 번째 한입",
                memo = "한 줄 메모",
            )
        }
    }
}

@Preview(name = "MjDiaryCard - 이번엔 못 했어요", widthDp = 360, showBackground = true)
@Composable
private fun MjDiaryCardMissedPreview() {
    MjHackathonTheme {
        Box(Modifier.background(MjHackathonTheme.colors.bgCanvas).padding(20.dp)) {
            MjDiaryCard(
                state = MjDiaryCardState.Missed,
                date = "2026.11.05",
                location = "영천시장",
                title = "시장에서 만나는 첫 번째 한입",
                memo = "한 줄 메모",
            )
        }
    }
}

@Preview(name = "MjDiaryCard - 메모 없음", widthDp = 360, showBackground = true)
@Composable
private fun MjDiaryCardNoMemoPreview() {
    MjHackathonTheme {
        MjDiaryCard(state = MjDiaryCardState.Did, date = "2026.11.05", location = "영천시장",
            title = "시장에서 만나는 첫 번째 한입", memo = "", modifier = Modifier.padding(20.dp))
    }
}

@Preview(name = "MjDiaryCard - 메모 원문 표시", widthDp = 360, showBackground = true)
@Composable
private fun MjDiaryCardFullMemoPreview() {
    MjHackathonTheme {
        MjDiaryCard(state = MjDiaryCardState.Missed, date = "2026.11.05", location = "영천시장",
            title = "시장에서 만나는 첫 번째 한입", memo = "오늘은 시장이 문을 닫아 다음에 다시 오기로 했다.\n다음 방문이 기대된다.",
            modifier = Modifier.padding(20.dp))
    }
}
