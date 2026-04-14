package com.develazquez.bibliocloud.features.audiobook.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.develazquez.bibliocloud.core.audio.AudioState
import com.develazquez.bibliocloud.features.audiobook.domain.usecases.GetPlaybackStateUseCase
import com.develazquez.bibliocloud.features.audiobook.domain.usecases.PauseAudioUseCase
import com.develazquez.bibliocloud.features.audiobook.domain.usecases.PlayAudioUseCase
import com.develazquez.bibliocloud.features.audiobook.domain.usecases.StopAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AudiobookViewModel @Inject constructor(
    private val playAudioUseCase: PlayAudioUseCase,
    private val pauseAudioUseCase: PauseAudioUseCase,
    private val stopAudioUseCase: StopAudioUseCase,
    private val getPlaybackStateUseCase: GetPlaybackStateUseCase
) : ViewModel() {

    val audioState: StateFlow<AudioState> = getPlaybackStateUseCase()

    fun play(url: String) {
        playAudioUseCase(url)
    }

    fun pause() {
        pauseAudioUseCase()
    }

    fun stop() {
        stopAudioUseCase()
    }
}
