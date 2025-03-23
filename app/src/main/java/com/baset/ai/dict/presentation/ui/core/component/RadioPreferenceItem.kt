package com.baset.ai.dict.presentation.ui.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.baset.ai.dict.presentation.ui.core.theme.Typography
import com.baset.ai.dict.presentation.ui.core.theme.margin8

@Composable
fun RadioPreference(
    modifier: Modifier,
    checked: Boolean,
    title: String,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onCheckedChange,
                    interactionSource = interactionSource,
                    indication = null
                )
                .weight(1f),
            text = title,
            fontWeight = FontWeight.Bold,
            style = Typography.bodyLarge
        )
        Spacer(modifier = Modifier.size(margin8))
        RadioButton(
            selected = checked,
            onClick = onCheckedChange
        )
    }
}

@Preview
@Composable
private fun RadioPreferencePreview() {
    RadioPreference(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        checked = false,
        title = "Model name",
        onCheckedChange = {}
    )
}
