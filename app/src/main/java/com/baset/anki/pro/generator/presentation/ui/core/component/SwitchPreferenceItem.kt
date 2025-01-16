package com.baset.anki.pro.generator.presentation.ui.core.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.baset.anki.pro.generator.presentation.ui.core.theme.Typography
import com.baset.anki.pro.generator.presentation.ui.core.theme.margin8

@Composable
fun SwitchPreference(
    modifier: Modifier,
    checked: Boolean,
    tile: String,
    description: String?,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(tile)
                }
                append("\n")
                append(description)
            },
            style = Typography.bodyLarge
        )
        Spacer(modifier = Modifier.size(margin8))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview
@Composable
private fun SwitchPreferencePreview() {
    SwitchPreference(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        checked = false,
        tile = "Window service",
        description = "Description",
        onCheckedChange = null
    )
}


