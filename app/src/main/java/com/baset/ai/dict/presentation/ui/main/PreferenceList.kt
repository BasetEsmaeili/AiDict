package com.baset.ai.dict.presentation.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import com.baset.ai.dict.R
import com.baset.ai.dict.common.Constants
import com.baset.ai.dict.presentation.ui.core.component.InputPreference
import com.baset.ai.dict.presentation.ui.core.component.OptionPreference
import com.baset.ai.dict.presentation.ui.core.component.SwitchPreference
import com.baset.ai.dict.presentation.ui.core.model.UiText
import com.baset.ai.dict.presentation.ui.core.theme.margin12
import com.baset.ai.dict.presentation.ui.core.theme.margin16
import com.baset.ai.dict.presentation.ui.core.theme.margin32
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PreferencesList(
    modifier: Modifier = Modifier,
    preferenceItems: ImmutableList<PreferenceItem>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(margin12),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(preferenceItems,
            key = { _, item -> item.id },
            contentType = { _, item -> item.type })
        { index, item ->
            when (item) {
                is PreferenceItem.Switch -> {
                    SwitchPreference(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        checked = item.checked,
                        tile = item.title.asString(),
                        description = item.description?.asString().orEmpty(),
                        onCheckedChange = {
                            item.onPreferenceSwitchCheckChange(item)
                        }
                    )
                }

                is PreferenceItem.OptionDialog -> {
                    OptionPreference(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        title = item.title.asString(),
                        description = item.description?.asString().orEmpty(),
                        options = item.options,
                        onOptionItemSelected = {
                            item.onPreferenceOptionItemSelected(item, it)
                        }
                    )
                }

                is PreferenceItem.Input -> {
                    InputPreference(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        title = item.title.asString(),
                        description = item.description?.asString().orEmpty(),
                        inputType = item.inputType,
                        value = item.text,
                        onDone = {
                            item.onInputPreferenceDone(item, it)
                        }
                    )
                }

                is PreferenceItem.CopyRight -> {
                    Spacer(modifier = Modifier.size(margin32))
                    IconButton(onClick = item.onOpenProjectOnGithubClicked) {
                        Icon(
                            painter = painterResource(R.drawable.ic_github_logo),
                            contentDescription = stringResource(R.string.content_description_github)
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.label_code_available_on))
                            append(Constants.SPACE)
                            withLink(
                                LinkAnnotation.Url(
                                    Constants.Intent.GITHUB_URI,
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = colorResource(
                                                R.color.purple_500
                                            )
                                        )
                                    )
                                )
                            ) {
                                append(stringResource(R.string.label_github))
                            }
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (index != preferenceItems.lastIndex) {
                Spacer(modifier = Modifier.size(margin16))
            }
        }
    }
}

@Preview
@Composable
private fun PreferencesModalBottomSheetContentPreview() {
    PreferencesList(
        preferenceItems = persistentListOf(
            PreferenceItem.Switch(
                "test_switch",
                checked = true,
                title = UiText.StringResource(R.string.title_window_service),
                description = UiText.StringResource(R.string.description_window_service),
                onPreferenceSwitchCheckChange = {}
            )
        )
    )
}