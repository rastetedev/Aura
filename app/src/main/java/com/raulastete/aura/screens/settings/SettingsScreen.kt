package com.raulastete.aura.screens.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raulastete.aura.R
import com.raulastete.aura.core.designsystem.modifier.defaultShadow
import com.raulastete.aura.core.designsystem.theme.AuraTheme
import com.raulastete.aura.core.designsystem.theme.bgGradient
import com.raulastete.aura.screens.settings.components.DataBackupCard
import com.raulastete.aura.screens.settings.components.DefaultTopicSelectorCard
import com.raulastete.aura.screens.settings.components.MoodCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onGoBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnBackClick -> onGoBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsUiState,
    onAction: (SettingsAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    // File picker for ZIP import
    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAction(SettingsAction.OnImportFileSelected(it)) }
    }

    // Notification permission launcher (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* permission result handled silently; worker shows toast regardless */ }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction(SettingsAction.OnBackClick)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.navigate_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = MaterialTheme.colorScheme.bgGradient)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            onAction(SettingsAction.OnRemoveFocusOnInputText)
                        }
                    )
                }
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MoodCard(
                selectedMood = state.selectedMood,
                onMoodClick = { onAction(SettingsAction.OnMoodClick(it)) },
                modifier = Modifier.defaultShadow(shape = RoundedCornerShape(8.dp))
            )

            DefaultTopicSelectorCard(
                topics = state.topics,
                searchText = state.searchText,
                topicSuggestions = state.suggestedTopics,
                showCreateTopicOption = state.showCreateTopicOption,
                showSuggestionsDropDown = state.isTopicSuggestionsVisible,
                canInputText = state.isTopicTextInputVisible,
                onSearchTextChange = { onAction(SettingsAction.OnSearchTextChange(it)) },
                onToggleCanInputText = { onAction(SettingsAction.OnAddButtonClick) },
                onAddTopicClick = { onAction(SettingsAction.OnSelectTopicClick(it)) },
                onRemoveTopicClick = { onAction(SettingsAction.OnRemoveTopicClick(it)) },
                onDismissSuggestionsDropDown = { onAction(SettingsAction.OnDismissTopicDropDown) },
                modifier = Modifier.defaultShadow(shape = RoundedCornerShape(8.dp))
            )

            DataBackupCard(
                isExporting = state.isExporting,
                isImporting = state.isImporting,
                onExportClick = {
                    // Request notification permission on Android 13+ before starting work
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    onAction(SettingsAction.OnExportClick)
                },
                onImportClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    importFileLauncher.launch("application/zip")
                },
                modifier = Modifier.defaultShadow(shape = RoundedCornerShape(8.dp))
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AuraTheme {
        SettingsContent(
            state = SettingsUiState(),
            onAction = {}
        )
    }
}
