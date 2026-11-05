package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
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

enum class MjResultSelectorType {
    NONE,
    DID,
    MISSED,
}

/** 선택 상태는 호출부에서 관리하며, 선택된 항목을 다시 눌러도 선택을 유지한다. */
@Composable
fun MjResultSelector(
    selectedType: MjResultSelectorType,
    onTypeSelected: (MjResultSelectorType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MjResultSelectorOption(
            text = "해봤어요!",
            type = MjResultSelectorType.DID,
            isSelected = selectedType == MjResultSelectorType.DID,
            onClick = { onTypeSelected(MjResultSelectorType.DID) },
            modifier = Modifier.weight(1f),
        )
        MjResultSelectorOption(
            text = "이번엔 못 했어요",
            type = MjResultSelectorType.MISSED,
            isSelected = selectedType == MjResultSelectorType.MISSED,
            onClick = { onTypeSelected(MjResultSelectorType.MISSED) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MjResultSelectorOption(
    text: String,
    type: MjResultSelectorType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MjHackathonTheme.colors
    val shape = RoundedCornerShape(16.dp)
    val contentColor = when {
        !isSelected -> colors.textSecondary
        type == MjResultSelectorType.DID -> colors.textDid
        else -> colors.textMissed
    }
    val backgroundColor = when {
        !isSelected -> colors.bgSurface
        type == MjResultSelectorType.DID -> colors.statusDidBg
        else -> colors.statusMissedBg
    }
    val borderColor = when {
        !isSelected -> colors.borderDefault
        type == MjResultSelectorType.DID -> colors.textDid
        else -> colors.borderStrong
    }

    Row(
        modifier = modifier
            .heightIn(min = 52.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
            .semantics(mergeDescendants = true) {
                selected = isSelected
                role = Role.RadioButton
            }
            .noRippleClickable(onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = contentColor,
            )
        }
        Text(
            text = text,
            color = contentColor,
            style = if (isSelected) {
                MjHackathonTheme.typography.body2Strong
            } else {
                MjHackathonTheme.typography.body2
            },
        )
    }
}

@Preview(name = "MjResultSelector - 미선택", widthDp = 328, showBackground = true)
@Composable
private fun MjResultSelectorNonePreview() {
    MjHackathonTheme {
        MjResultSelector(
            selectedType = MjResultSelectorType.NONE,
            onTypeSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "MjResultSelector - 해봤어요", widthDp = 328, showBackground = true)
@Composable
private fun MjResultSelectorDidPreview() {
    MjHackathonTheme {
        MjResultSelector(
            selectedType = MjResultSelectorType.DID,
            onTypeSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "MjResultSelector - 이번엔 못 했어요", widthDp = 328, showBackground = true)
@Composable
private fun MjResultSelectorMissedPreview() {
    MjHackathonTheme {
        MjResultSelector(
            selectedType = MjResultSelectorType.MISSED,
            onTypeSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
