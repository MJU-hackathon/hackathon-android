package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable

@Composable
fun MjSmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (enabled) MjHackathonTheme.colors.actionPrimary else MjHackathonTheme.colors.actionDisabled,
            )
            .semantics(mergeDescendants = true) {
                role = Role.Button
                if (!enabled) disabled()
            }
            .then(if (enabled) Modifier.noRippleClickable(onClick) else Modifier)
            .padding(horizontal = 32.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) MjHackathonTheme.colors.textOnBrand else MjHackathonTheme.colors.textDisabled,
            style = MjHackathonTheme.typography.buttonL,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "MjSmallButton - 활성", showBackground = true)
@Composable
private fun MjSmallButtonEnabledPreview() {
    MjHackathonTheme {
        MjSmallButton(text = "버튼", onClick = {})
    }
}

@Preview(name = "MjSmallButton - 비활성", showBackground = true)
@Composable
private fun MjSmallButtonDisabledPreview() {
    MjHackathonTheme {
        MjSmallButton(text = "버튼", onClick = {}, enabled = false)
    }
}

