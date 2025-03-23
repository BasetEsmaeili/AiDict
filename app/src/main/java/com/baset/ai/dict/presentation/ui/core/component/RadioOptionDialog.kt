package com.baset.ai.dict.presentation.ui.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.baset.ai.dict.presentation.ui.core.theme.margin16
import com.baset.ai.dict.presentation.ui.main.PreferenceItem
import com.baset.ai.dict.presentation.ui.main.PreferencesList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun RadioOptionDialog(
    modifier: Modifier,
    options: ImmutableList<PreferenceItem>,
    onPreferenceItemClicked: (PreferenceItem) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AiDictDialog(onDismissRequest) {
        Card(
            modifier = modifier
                .clickable(
                    enabled = false,
                    onClick = {}
                )
        ) {
            PreferencesList(
                preferenceItems = options,
                onPreferenceItemClicked = onPreferenceItemClicked
            )
        }
    }
}

@Preview
@Composable
private fun RadioOptionDialogPreview() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    RadioOptionDialog(
        modifier = Modifier
            .padding(margin16)
            .fillMaxWidth()
            .height(screenWidth / 2),
        options = persistentListOf(),
        onPreferenceItemClicked = {},
        onDismissRequest = { }
    )
}
