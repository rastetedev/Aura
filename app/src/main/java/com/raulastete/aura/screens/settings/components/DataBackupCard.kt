package com.raulastete.aura.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raulastete.aura.R
import com.raulastete.aura.core.designsystem.theme.AuraTheme

@Composable
fun DataBackupCard(
    isExporting: Boolean,
    isImporting: Boolean,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = stringResource(R.string.backup_card_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.backup_card_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Export button
                Button(
                    onClick = onExportClick,
                    enabled = !isExporting && !isImporting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AnimatedVisibility(visible = isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    AnimatedVisibility(visible = !isExporting) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp)
                        )
                    }
                    Text(
                        text = if (isExporting) stringResource(R.string.backup_exporting)
                        else stringResource(R.string.backup_export)
                    )
                }

                // Import button
                OutlinedButton(
                    onClick = onImportClick,
                    enabled = !isExporting && !isImporting,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    AnimatedVisibility(visible = isImporting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    AnimatedVisibility(visible = !isImporting) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp)
                        )
                    }
                    Text(
                        text = if (isImporting) stringResource(R.string.backup_importing)
                        else stringResource(R.string.backup_import)
                    )
                }
            }

            AnimatedVisibility(visible = isExporting || isImporting) {
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = if (isExporting) stringResource(R.string.backup_export_in_progress_hint)
                    else stringResource(R.string.backup_import_in_progress_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
private fun DataBackupCardPreview() {
    AuraTheme {
        DataBackupCard(
            isExporting = false,
            isImporting = false,
            onExportClick = {},
            onImportClick = {}
        )
    }
}

@Preview
@Composable
private fun DataBackupCardExportingPreview() {
    AuraTheme {
        DataBackupCard(
            isExporting = true,
            isImporting = false,
            onExportClick = {},
            onImportClick = {}
        )
    }
}
