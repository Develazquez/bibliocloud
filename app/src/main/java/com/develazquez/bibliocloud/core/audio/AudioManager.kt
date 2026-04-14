package com.develazquez.bibliocloud.core.audio

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AudioState {
    IDLE, LOADING, PLAYING, PAUSED, ERROR
}

@Singleton
class AudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val _audioState = MutableStateFlow(AudioState.IDLE)
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, AudioService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                controller = controllerFuture?.get()
                controller?.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        updateState()
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updateState()
                    }
                })
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    private fun updateState() {
        val player = controller ?: return
        val state = when {
            player.playbackState == Player.STATE_BUFFERING -> AudioState.LOADING
            player.isPlaying -> AudioState.PLAYING
            !player.isPlaying && player.playbackState == Player.STATE_READY -> AudioState.PAUSED
            player.playbackState == Player.STATE_IDLE -> AudioState.IDLE
            else -> AudioState.IDLE
        }
        _audioState.value = state
    }

    fun playAudio(url: String) {
        val mediaController = controller ?: return
        val mediaItem = MediaItem.fromUri(url)
        mediaController.setMediaItem(mediaItem)
        mediaController.prepare()
        mediaController.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun stop() {
        controller?.stop()
        controller?.clearMediaItems()
    }
}
