package com.baset.ai.dict.presentation.ui.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.baset.ai.dict.R
import com.baset.ai.dict.presentation.ui.core.theme.Typography
import com.baset.ai.dict.presentation.ui.core.theme.margin12
import com.baset.ai.dict.presentation.ui.core.theme.margin16
import com.baset.ai.dict.presentation.ui.core.theme.margin8
import com.baset.ai.dict.presentation.ui.main.OptionItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun OptionPreference(
    modifier: Modifier,
    title: String,
    description: String?,
    options: ImmutableList<OptionItem>,
    onOptionItemSelected: (selectedItem: OptionItem) -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var showOptions by remember { mutableStateOf(false) }
    if (showOptions) {
        SelectOptionDialog(
            modifier = Modifier
                .padding(margin16)
                .fillMaxWidth()
                .height(screenHeight / 2),
            options = options,
            onDismissRequest = { showOptions = false },
            onOptionItemSelected = onOptionItemSelected
        )
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val selectedItem by remember(options) {
            derivedStateOf {
                options.firstOrNull { it.selected }
            }
        }
        val interactionSource = remember { MutableInteractionSource() }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.7f),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(title)
                }
                if (!description.isNullOrEmpty()) {
                    append("\n")
                    append(description)
                }
            },
            style = Typography.bodyLarge
        )
        Spacer(modifier = Modifier.size(margin8))
        Text(
            modifier = Modifier
                .weight(.3f)
                .clickable(
                    onClick = {
                        showOptions = true
                    },
                    indication = null,
                    interactionSource = interactionSource
                ),
            textAlign = TextAlign.End,
            text = selectedItem?.text ?: stringResource(R.string.label_none)
        )
    }
}

@Composable
private fun SelectOptionDialog(
    modifier: Modifier,
    options: ImmutableList<OptionItem>,
    onDismissRequest: () -> Unit,
    onOptionItemSelected: (selectedItem: OptionItem) -> Unit
) {
    AiDictDialog(onDismissRequest) {
        Card(
            modifier = modifier
                .clickable(
                    enabled = false,
                    onClick = {}
                )
        ) {
            LazyColumn {
                items(options) { option ->
                    Text(
                        modifier = Modifier
                            .clickable(onClick = {
                                onOptionItemSelected(option)
                                onDismissRequest()
                            })
                            .padding(margin12)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        text = option.text,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = margin12))
                }
            }
        }
    }
}

@Preview
@Composable
private fun InputPreferencePreview() {
    OptionPreference(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        title = "Window service",
        description = "Description",
        options = persistentListOf(OptionItem("id", true, "Hello World!")),
        onOptionItemSelected = {}
    )
}


