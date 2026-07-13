package com.raulastete.aura.screens.record_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raulastete.aura.R
import com.raulastete.aura.core.presentation.designsystem.theme.AuraTheme
import com.raulastete.aura.core.presentation.designsystem.theme.bgGradient
import com.raulastete.aura.screens.record_list.components.NoRecordsView
import com.raulastete.aura.screens.record_list.components.RecordFab

@Composable
fun RecordListScreen(
    viewModel: RecordListViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecordListContent(
        state = state,
        onSettingsClick = {},
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListContent(
    state: RecordListUiState,
    onSettingsClick: () -> Unit,
    onAction: (RecordListAction) -> Unit,
) {
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
            RecordFab(onClick = { onAction(RecordListAction.OnFabClick) })
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
                    NoRecordsView(
                        Modifier
                            .weight(1f)
                            .wrapContentSize()
                            .align(Alignment.CenterHorizontally)
                    )
                }
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