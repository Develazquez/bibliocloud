package com.develazquez.bibliocloud.features.audiobook.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.develazquez.bibliocloud.core.audio.AudioState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AudioPlayerControls(
    audioUrl: String?,
    audioStateFlow: StateFlow<AudioState>,
    onPlayClick: (String) -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {
    val state by audioStateFlow.collectAsState()

    if (audioUrl == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (state) {
                AudioState.LOADING -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
                AudioState.PLAYING -> {
                    IconButton(onClick = onPauseClick) {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = "Pausar", modifier = Modifier.size(48.dp))
                    }
                }
                else -> {
                    IconButton(onClick = { onPlayClick(audioUrl) }) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Reproducir", modifier = Modifier.size(48.dp))
                    }
                }
            }

            IconButton(onClick = onStopClick, enabled = state == AudioState.PLAYING || state == AudioState.PAUSED) {
                Icon(imageVector = Icons.Default.Stop, contentDescription = "Detener", modifier = Modifier.size(48.dp))
            }
        }
    }
}
