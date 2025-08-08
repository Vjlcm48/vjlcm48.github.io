package com.heptacreation.sumamente.ui.utils

import android.content.Context
import android.media.MediaPlayer

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null


    fun play(context: Context, resId: Int, looping: Boolean = true, volume: Float = 1.0f) {

        if (mediaPlayer == null || currentResId != resId) {
            stop()
            mediaPlayer = MediaPlayer.create(context.applicationContext, resId)
            currentResId = resId
            mediaPlayer?.isLooping = looping
            mediaPlayer?.setVolume(volume, volume)
        }
        if (mediaPlayer?.isPlaying == false) {
            try { mediaPlayer?.start() } catch (_: Exception) {}
        }
    }

    fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun resume() {
        mediaPlayer?.takeIf { !it.isPlaying }?.start()
    }

    fun stop() {
        try { mediaPlayer?.stop() } catch (_: Exception) {}
        try { mediaPlayer?.release() } catch (_: Exception) {}
        mediaPlayer = null
        currentResId = null
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

}
