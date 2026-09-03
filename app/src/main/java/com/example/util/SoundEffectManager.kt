package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * SoundEffectManager provides a calm, pleasant, non-disturbing subtle sound effect
 * for button clicks and interactions across the application.
 */
object SoundEffectManager {
  private var isSoundEnabled: Boolean = true
  private var tapPcmData: ByteArray? = null

  init {
    try {
      tapPcmData = generateSoftTapPcm()
    } catch (_: Exception) {}
  }

  fun setSoundEnabled(enabled: Boolean) {
    isSoundEnabled = enabled
  }

  fun isSoundEnabled(): Boolean = isSoundEnabled

  /**
   * Generates a warm, subtle, non-intrusive micro-pop sound.
   * Duration: ~28ms
   * Frequency: 520Hz gliding smoothly down to 380Hz
   * Decay: Exponential envelope with soft onset and smooth zero-crossing tail
   */
  private fun generateSoftTapPcm(): ByteArray {
    val sampleRate = 44100
    val durationMs = 28
    val numSamples = (sampleRate * durationMs / 1000)
    val pcm = ByteArray(numSamples * 2)
    val baseFreq = 520.0

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      // Fast exponential decay for a crisp yet warm and gentle click
      val attack = (i.toDouble() / (sampleRate * 0.003)).coerceAtMost(1.0) // 3ms soft attack
      val decay = exp(-t * 95.0)
      val envelope = attack * decay
      val freq = baseFreq * (1.0 - t * 8.0).coerceAtLeast(0.65)
      val sampleFloat = sin(2.0 * PI * freq * t) * envelope * 0.28 * Short.MAX_VALUE
      val sampleShort = sampleFloat.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

      pcm[i * 2] = (sampleShort.toInt() and 0xFF).toByte()
      pcm[i * 2 + 1] = ((sampleShort.toInt() shr 8) and 0xFF).toByte()
    }
    return pcm
  }

  /**
   * Plays the subtle tap sound effect immediately.
   */
  fun playTapSound(context: Context? = null) {
    if (!isSoundEnabled) return

    try {
      val pcm = tapPcmData ?: generateSoftTapPcm().also { tapPcmData = it }
      val sampleRate = 44100

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(pcm.size)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(pcm, 0, pcm.size)
      track.play()

      // Auto-cleanup track after playback
      track.setNotificationMarkerPosition(pcm.size / 2)
      track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
        override fun onMarkerReached(trackRef: AudioTrack?) {
          try {
            trackRef?.stop()
            trackRef?.release()
          } catch (_: Exception) {}
        }
        override fun onPeriodicNotification(trackRef: AudioTrack?) {}
      })
    } catch (_: Exception) {
      // Graceful fallback to subtle system click if audio track is unavailable
      try {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.3f)
      } catch (_: Exception) {}
    }
  }

  /**
   * Generates a warm, uplifting two-tone chime for success / reward unlocked.
   */
  private fun generateSuccessChimePcm(): ByteArray {
    val sampleRate = 44100
    val durationMs = 180
    val numSamples = (sampleRate * durationMs / 1000)
    val pcm = ByteArray(numSamples * 2)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      val freq = if (t < 0.08) 587.33 else 880.0 // D5 to A5
      val localT = if (t < 0.08) t else t - 0.08
      val decay = exp(-localT * 30.0)
      val attack = (localT / 0.005).coerceAtMost(1.0)
      val envelope = attack * decay
      val sampleFloat = sin(2.0 * PI * freq * t) * envelope * 0.25 * Short.MAX_VALUE
      val sampleShort = sampleFloat.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

      pcm[i * 2] = (sampleShort.toInt() and 0xFF).toByte()
      pcm[i * 2 + 1] = ((sampleShort.toInt() shr 8) and 0xFF).toByte()
    }
    return pcm
  }

  /**
   * Plays a pleasant celebratory chime when rewards or perks are unlocked.
   */
  fun playSuccessSound(context: Context? = null) {
    if (!isSoundEnabled) return
    try {
      val pcm = generateSuccessChimePcm()
      val sampleRate = 44100

      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(pcm.size)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(pcm, 0, pcm.size)
      track.play()

      track.setNotificationMarkerPosition(pcm.size / 2)
      track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
        override fun onMarkerReached(trackRef: AudioTrack?) {
          try {
            trackRef?.stop()
            trackRef?.release()
          } catch (_: Exception) {}
        }
        override fun onPeriodicNotification(trackRef: AudioTrack?) {}
      })
    } catch (_: Exception) {
      try {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.5f)
      } catch (_: Exception) {}
    }
  }
}
