package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable

/** 썸네일을 생략하면 갤러리 아이콘을 표시한다. 화면 이동은 onClick에서 처리한다. */
@Composable
fun MjQuestCard(
    title: String,
    category: String,
    location: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes thumbnailRes: Int? = null,
) {
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = shape)
            .clip(shape)
            .background(colors.bgSurface)
            .noRippleClickable(onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.bgMuted),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbnailRes == null) {
                Icon(
                    painter = painterResource(R.drawable.ic_gallery),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = colors.iconSecondary,
                )
            } else {
                Image(
                    painter = painterResource(thumbnailRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = category,
                modifier = Modifier
                    .background(colors.bgAccentSubtle, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = colors.textAccent,
                style = typography.captionStrong,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = title,
                color = colors.textPrimary,
                style = typography.body1Strong,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_map_pin),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = colors.iconSecondary,
                )
                Text(
                    text = location,
                    color = colors.textTertiary,
                    style = typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.ic_right_arrow),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = colors.iconSecondary,
        )
    }
}

@Preview(name = "MjQuestCard - 기본", widthDp = 304, showBackground = true)
@Composable
private fun MjQuestCardPreview() {
    MjHackathonTheme {
        Box(Modifier.background(MjHackathonTheme.colors.bgCanvas).padding(20.dp)) {
            MjQuestCard(
                title = "시장에서 만나는 첫 번째 한입",
                category = "음식",
                location = "영천시장",
                onClick = {},
            )
        }
    }
}

@Preview(name = "MjQuestCard - 긴 텍스트", widthDp = 304, showBackground = true)
@Composable
private fun MjQuestCardLongTextPreview() {
    MjHackathonTheme {
        Box(Modifier.background(MjHackathonTheme.colors.bgCanvas).padding(20.dp)) {
            MjQuestCard(
                title = "전통시장에서 다양한 먹거리를 찾아 나만의 새로운 한 끼를 즐겨 보세요",
                category = "로컬 음식 탐방",
                location = "서울특별시 서대문구 영천시장 입구",
                onClick = {},
            )
        }
    }
}
