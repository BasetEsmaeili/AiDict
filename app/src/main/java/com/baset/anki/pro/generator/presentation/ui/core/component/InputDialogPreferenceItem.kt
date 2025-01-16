package com.baset.anki.pro.generator.presentation.ui.core.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.baset.anki.pro.generator.R
import com.baset.anki.pro.generator.presentation.ui.core.theme.Typography
import com.baset.anki.pro.generator.presentation.ui.core.theme.margin12
import com.baset.anki.pro.generator.presentation.ui.core.theme.margin16
import com.baset.anki.pro.generator.presentation.ui.core.theme.margin8
import com.baset.anki.pro.generator.presentation.ui.main.OptionItem
import com.baset.anki.pro.generator.presentation.ui.main.PreferenceItem
import kotlinx.collections.immutable.persistentListOf

@Composable
fun InputPreference(
    modifier: Modifier,
    title: String,
    description: String?,
    inputType: PreferenceItem.Input.InputType,
    value: String?,
    onDone: (String) -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }
    if (showOptions) {
        InputDialog(
            modifier = Modifier
                .padding(margin16)
                .fillMaxWidth()
                .wrapContentHeight(),
            inputType = inputType,
            value = value,
            onDismissRequest = { showOptions = false },
            onDone = onDone
        )
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                append("\n")
                append(description)
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
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            text = value ?: stringResource(R.string.label_none)
        )
    }
}

@Composable
private fun InputDialog(
    modifier: Modifier,
    inputType: PreferenceItem.Input.InputType,
    value: String?,
    onDismissRequest: () -> Unit,
    onDone: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        val submit: (String) -> Unit = remember {
            {
                onDismissRequest()
                onDone(it)
            }
        }
        val interactionSource = remember { MutableInteractionSource() }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = onDismissRequest
                )
        ) {
            Card(
                modifier = modifier
                    .clickable(
                        enabled = false,
                        onClick = {}
                    )
            ) {
                var textFieldValue by remember { mutableStateOf(TextFieldValue(value.orEmpty())) }
                OutlinedTextField(
                    modifier = Modifier
                        .padding(margin12)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    value = textFieldValue,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = when (inputType) {
                            PreferenceItem.Input.InputType.Text -> KeyboardType.Text
                            PreferenceItem.Input.InputType.Number -> KeyboardType.Number
                        },
                        imeAction = ImeAction.Done,
                        showKeyboardOnFocus = true,
                        hintLocales = LocaleList(Locale.current)
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        submit.invoke(textFieldValue.text)
                    }),
                    label = {
                        Text(text = stringResource(R.string.label_enter_value))
                    },
                    onValueChange = {
                        textFieldValue = it
                    })
                Button(
                    modifier = Modifier
                        .padding(margin12)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onClick = { submit.invoke(textFieldValue.text) },
                ) {
                    Text(stringResource(R.string.label_submit))
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

