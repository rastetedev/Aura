package com.raulastete.aura.screens.record_list

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raulastete.aura.R
import com.raulastete.aura.core.domain.recording.RecordingDetails
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.core.presentation.designsystem.theme.bgGradient
import com.raulastete.aura.core.presentation.util.lifecycle.ObserveAsEvents
import com.raulastete.aura.core.presentation.util.lifecycle.isAppInForeground
import com.raulastete.aura.screens.record_list.components.FiltersSection
import com.raulastete.aura.screens.record_list.components.NoRecordsView
import com.raulastete.aura.screens.record_list.components.QuickRecordFabButton
import com.raulastete.aura.screens.record_list.components.RecordList
import com.raulastete.aura.screens.record_list.components.RecordingSheet
import com.raulastete.aura.screens.record_list.model.AudioCaptureMethod
import com.raulastete.aura.screens.record_list.model.RecordingState
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordListScreen(
    viewModel: RecordListViewModel = koinViewModel(),
    onNavigateToCreateRecord: (RecordingDetails) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted && state.currentCaptureMethod == AudioCaptureMethod.STANDARD) {
                viewModel.onAction(RecordListAction.OnAudioPermissionGranted)
            }
        }
    )

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            RecordListEvent.RequestAudioPermission -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }

            is RecordListEvent.RecordingTooShort -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.audio_recording_was_too_short),
                    Toast.LENGTH_LONG
                ).show()
            }

            is RecordListEvent.OnDoneRecording -> {
                onNavigateToCreateRecord(event.details)
            }
        }
    }

    val isAppInForeground by isAppInForeground()
    LaunchedEffect(isAppInForeground, state.recordingState) {
        if(state.recordingState == RecordingState.NORMAL_CAPTURE && isAppInForeground.not()){
            viewModel.onAction(RecordListAction.OnPauseRecordingClick)
        }
    }

    RecordListContent(
        state = state,
        onSettingsClick = onNavigateToSettings,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordListContent(
    state: RecordListUiState,
    onSettingsClick: () -> Unit,
    onAction: (RecordListAction) -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.record_list_screen_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            QuickRecordFabButton(
                onClick = { onAction(RecordListAction.OnRecordFabClick) },
                isQuickRecording = state.recordingState == RecordingState.QUICK_CAPTURE,
                onLongPressEnd = { cancelledRecording ->
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

                    if(hasPermission.not()) return@QuickRecordFabButton

                    if(cancelledRecording) {
                        onAction(RecordListAction.OnCancelRecording)
                    } else {
                        onAction(RecordListAction.OnCompleteRecording)
                    }
                },
                onLongPressStart = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                    if(hasPermission) {
                        onAction(RecordListAction.OnRecordButtonLongClick)
                    } else {
                        onAction(RecordListAction.OnRequestPermissionQuickRecording)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.bgGradient)
                .padding(innerPadding)
        ) {

            when {
                state.isLoadingData -> {
                    CircularProgressIndicator(
                        Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.hasRecords.not() -> {
                    Column(Modifier.fillMaxWidth()) {
                        FiltersSection(
                            modifier = Modifier.padding(16.dp),
                            moods = state.moodFilterList,
                            topics = state.topicFilterList,
                            moodChipContent = state.moodChipContent,
                            topicChipContent = state.topicChipContent,
                            isMoodFilterActive = state.isMoodFilterActive,
                            isTopicFilterActive = state.isTopicFilterActive,
                            onAction = onAction,
                        )

                        NoRecordsView(
                            Modifier
                                .weight(1f)
                                .wrapContentSize()
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                else -> {

                    Column(Modifier.fillMaxWidth()) {
                        FiltersSection(
                            modifier = Modifier.padding(16.dp),
                            moods = state.moodFilterList,
                            topics = state.topicFilterList,
                            moodChipContent = state.moodChipContent,
                            topicChipContent = state.topicChipContent,
                            isMoodFilterActive = state.isMoodFilterActive,
                            isTopicFilterActive = state.isTopicFilterActive,
                            onAction = onAction,
                        )

                        RecordList(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            recordSections = state.recordSections,
                            onPlayClick = { onAction(RecordListAction.OnPlayClick(it)) },
                            onPauseClick = { onAction(RecordListAction.OnPauseAudioClick) },
                            onTrackSizeAvailable = {
                                onAction(
                                    RecordListAction.OnTrackSizeAvailable(
                                        it
                                    )
                                )
                            },
                        )
                    }
                }
            }

            if (state.recordingState in listOf(
                    RecordingState.NORMAL_CAPTURE,
                    RecordingState.PAUSED
                )
            ) {
                RecordingSheet(
                    formattedRecordDuration = state.formattedRecordDuration,
                    isRecording = state.recordingState == RecordingState.NORMAL_CAPTURE,
                    onDismiss = { onAction(RecordListAction.OnCancelRecording) },
                    onPauseClick = { onAction(RecordListAction.OnPauseRecordingClick) },
                    onResumeClick = { onAction(RecordListAction.OnResumeRecordingClick) },
                    onCompleteRecording = { onAction(RecordListAction.OnCompleteRecording) },
                )
            }
        }
    }
}


@Preview
@Composable
private fun Preview() {
    AuraTheme {
        RecordListContent(
            state = RecordListUiState(isLoadingData = false),
            onSettingsClick = {},
            onAction = {}
        )
    }
}