package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable

@Composable
fun MjChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    val textColor = if (selected) {
        MjHackathonTheme.colors.textBrand
    } else {
        MjHackathonTheme.colors.textPrimary
    }

    Row(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) MjHackathonTheme.colors.bgBrandSubtle else MjHackathonTheme.colors.bgSurface,
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MjHackathonTheme.colors.borderBrand else MjHackathonTheme.colors.borderDefault,
                shape = shape,
            )
            .semantics(mergeDescendants = true) {
                this.selected = selected
                role = Role.Checkbox
            }
            .noRippleClickable(onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MjHackathonTheme.colors.iconBrand,
            )
        }

        Text(
            text = text,
            color = textColor,
            style = if (selected) {
                MjHackathonTheme.typography.buttonL
            } else {
                MjHackathonTheme.typography.body1
            },
        )
    }
}

@Preview(name = "MjChip - 선택", showBackground = true)
@Composable
private fun MjChipSelectedPreview() {
    MjHackathonTheme {
        MjChip(text = "라벨", selected = true, onClick = {})
    }
}

@Preview(name = "MjChip - 미선택", showBackground = true)
@Composable
private fun MjChipUnselectedPreview() {
    MjHackathonTheme {
        MjChip(text = "라벨", selected = false, onClick = {})
    }
}

