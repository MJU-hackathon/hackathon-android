package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable

enum class MjNavigationBarItem(
    val text: String,
    @param:DrawableRes val iconRes: Int,
) {
    Explore("동네 탐험", R.drawable.ic_compass),
    Diary("나의 유학 일기", R.drawable.ic_book),
    Profile("내 정보", R.drawable.ic_user),
}

/** 시스템 내비게이션 영역 여백은 상위 Scaffold에서 처리한다. */
@Composable
fun MjNavigationBar(
    selectedItem: MjNavigationBarItem,
    onItemClick: (MjNavigationBarItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MjHackathonTheme.colors.bgSurface)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MjNavigationBarItem.entries.forEach { item ->
            val isSelected = item == selectedItem
            val textColor = if (isSelected) {
                MjHackathonTheme.colors.textBrand
            } else {
                MjHackathonTheme.colors.textTertiary
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .semantics(mergeDescendants = true) {
                        selected = isSelected
                        role = Role.Tab
                    }
                    .noRippleClickable { onItemClick(item) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 32.dp)
                        .background(
                            color = if (isSelected) MjHackathonTheme.colors.bgBrandSubtle else Color.Transparent,
                            shape = RoundedCornerShape(50),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) {
                            MjHackathonTheme.colors.textBrand
                        } else {
                            MjHackathonTheme.colors.iconSecondary
                        },
                    )
                }

                Text(
                    text = item.text,
                    color = textColor,
                    style = if (isSelected) {
                        MjHackathonTheme.typography.captionStrong
                    } else {
                        MjHackathonTheme.typography.caption
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(name = "NavigationBar - 동네 탐험", widthDp = 360, showBackground = true)
@Composable
private fun MjNavigationBarExplorePreview() {
    MjHackathonTheme {
        MjNavigationBar(selectedItem = MjNavigationBarItem.Explore, onItemClick = {})
    }
}

@Preview(name = "NavigationBar - 나의 유학 일기", widthDp = 360, showBackground = true)
@Composable
private fun MjNavigationBarDiaryPreview() {
    MjHackathonTheme {
        MjNavigationBar(selectedItem = MjNavigationBarItem.Diary, onItemClick = {})
    }
}

@Preview(name = "NavigationBar - 내 정보", widthDp = 360, showBackground = true)
@Composable
private fun MjNavigationBarProfilePreview() {
    MjHackathonTheme {
        MjNavigationBar(selectedItem = MjNavigationBarItem.Profile, onItemClick = {})
    }
}

