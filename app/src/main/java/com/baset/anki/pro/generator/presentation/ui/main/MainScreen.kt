package com.baset.anki.pro.generator.presentation.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.baset.anki.pro.generator.R
import com.baset.anki.pro.generator.presentation.ui.core.theme.Typography

@Composable
fun MainRoute(
    modifier: Modifier,
    mainViewModel: MainViewModel
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    MainScreen(
        modifier = modifier,
        preferencesUiState = uiState.preferencesUiState,
        onPreferenceSwitchCheckChange = remember(mainViewModel) { mainViewModel::onPreferenceSwitchCheckChange },
        onPreferenceOptionItemSelected = remember(mainViewModel) { mainViewModel::onPreferenceOptionItemSelected },
        onInputPreferenceDone = remember(mainViewModel) { mainViewModel::onInputPreferenceDone },
        onOpenProjectOnGithubClicked = remember(mainViewModel) { mainViewModel::onOpenProjectOnGithubClicked }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    preferencesUiState: PreferencesUiState,
    onPreferenceSwitchCheckChange: (PreferenceItem) -> Unit,
    onPreferenceOptionItemSelected: (preferenceItem: PreferenceItem, optionItem: OptionItem) -> Unit,
    onInputPreferenceDone: (PreferenceItem, String) -> Unit,
    onOpenProjectOnGithubClicked: () -> Unit
) {
    var showOptionsBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = Typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        showOptionsBottomSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_description_preferences)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showOptionsBottomSheet) {
            ModalBottomSheet(modifier = Modifier.padding(innerPadding),
                onDismissRequest = {
                    showOptionsBottomSheet = false
                }) {
                PreferencesModalBottomSheetContent(
                    preferenceItems = preferencesUiState.preferenceItems,
                    onPreferenceSwitchCheckChange = onPreferenceSwitchCheckChange,
                    onPreferenceOptionItemSelected = onPreferenceOptionItemSelected,
                    onInputPreferenceDone = onInputPreferenceDone,
                    onOpenProjectOnGithubClicked = onOpenProjectOnGithubClicked
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

        }
    }
}

@Preview
@Composable
private fun MainPreview() {
    MainScreen(
        preferencesUiState = PreferencesUiState(),
        onPreferenceSwitchCheckChange = {},
        onPreferenceOptionItemSelected = { _, _ -> },
        onInputPreferenceDone = { _, _ -> },
        onOpenProjectOnGithubClicked = {}
    )
}