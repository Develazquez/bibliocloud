package com.develazquez.bibliocloud.features.audiobook.domain.usecases

import com.develazquez.bibliocloud.core.audio.AudioManager
import com.develazquez.bibliocloud.core.audio.AudioState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PlayAudioUseCase @Inject constructor(private val audioManager: AudioManager) {
    operator fun invoke(url: String) = audioManager.playAudio(url)
}

class PauseAudioUseCase @Inject constructor(private val audioManager: AudioManager) {
    operator fun invoke() = audioManager.pause()
}

class StopAudioUseCase @Inject constructor(private val audioManager: AudioManager) {
    operator fun invoke() = audioManager.stop()
}

class GetPlaybackStateUseCase @Inject constructor(private val audioManager: AudioManager) {
    operator fun invoke(): StateFlow<AudioState> = audioManager.audioState
}
