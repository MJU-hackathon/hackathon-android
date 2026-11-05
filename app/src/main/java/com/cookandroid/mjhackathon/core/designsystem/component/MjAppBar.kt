package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.cookandroid.mjhackathon.core.util.noRippleClickable

enum class MjAppBarType {
    Logo,
    Back,
    Title,
}

/**
 * Logo: 위치 아이콘과 브랜드명, Back: 뒤로가기와 제목, Title: 제목만 표시한다.
 * 시스템 영역 여백은 상위 Scaffold에서 처리한다.
 */
@Composable
fun MjAppBar(
    type: MjAppBarType,
    text: String = if (type == MjAppBarType.Logo) "K-log" else "",
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MjHackathonTheme.colors.bgCanvas)
            .padding(horizontal = if (type == MjAppBarType.Back) 4.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (type) {
            MjAppBarType.Logo -> Unit

            MjAppBarType.Back -> {
                Box(
                    modifier = Modifier.size(48.dp).noRippleClickable(onBackClick),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp).align(Alignment.Center),
                        tint = MjHackathonTheme.colors.iconDefault,
                    )
                }
            }

            MjAppBarType.Title -> Unit
        }

        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(end = if (type == MjAppBarType.Back) 16.dp else 0.dp),
            color = MjHackathonTheme.colors.textPrimary
            ,
            style = if (type == MjAppBarType.Back) {
                MjHackathonTheme.typography.title3
            } else {
                MjHackathonTheme.typography.title2
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(name = "AppBar - 로고", widthDp = 360, showBackground = true)
@Composable
private fun MjAppBarLogoPreview() {
    MjHackathonTheme {
        MjAppBar(type = MjAppBarType.Logo)
    }
}

@Preview(name = "AppBar - 뒤로가기와 제목", widthDp = 360, showBackground = true)
@Composable
private fun MjAppBarBackPreview() {
    MjHackathonTheme {
        MjAppBar(type = MjAppBarType.Back, text = "제목", onBackClick = {})
    }
}

@Preview(name = "AppBar - 제목", widthDp = 360, showBackground = true)
@Composable
private fun MjAppBarTitlePreview() {
    MjHackathonTheme {
        MjAppBar(type = MjAppBarType.Title, text = "제목")
    }
}

@Preview(name = "AppBar - 긴 제목", widthDp = 360, showBackground = true)
@Composable
private fun MjAppBarLongTitlePreview() {
    MjHackathonTheme {
        MjAppBar(type = MjAppBarType.Back, text = "한 줄을 넘어가는 긴 제목은 말줄임으로 표시합니다")
    }
}

