/*
 * Choona - Guitar Tuner
 * Copyright (C) 2026 Utkarsh Verma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rohankhayech.choona.lib.controller.tuner

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.TarsosDSPAudioInputStream
import com.rohankhayech.choona.lib.model.tuning.Notes
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Opens the microphone for pitch detection.
 *
 * [MediaRecorder.AudioSource.MIC] on recent Pixels runs through speech
 * processing, which holds a note and then releases it in one jump. The
 * unprocessed source avoids that, but only while the record buffer stays at
 * the device minimum and the rate is the native rate.
 */
internal object MicrophoneCapture {
    private const val FALLBACK_SAMPLE_RATE = 22050

    data class Opened(
        val dispatcher: AudioDispatcher,
        val sampleRate: Float,
        val analysisSamples: Int,
    )

    fun open(context: Context): Opened {
        val nativeRate = context.getSystemService(AudioManager::class.java)
            .getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
            ?.toIntOrNull()
            ?.takeIf { it > 0 }
            ?: 48000
        val attempts = listOf(
            MediaRecorder.AudioSource.UNPROCESSED to nativeRate,
            MediaRecorder.AudioSource.MIC to FALLBACK_SAMPLE_RATE
        )
        var lastError: Exception? = null
        for ((source, sampleRate) in attempts) {
            try {
                return open(source, sampleRate)
            } catch (e: Exception) {
                lastError = e
            }
        }
        throw lastError ?: IllegalStateException("Unable to open microphone")
    }

    /** Yin needs two periods of the lowest note in the window. */
    fun analysisSamples(sampleRate: Int, lowestFrequencyHz: Double): Int {
        val required = (sampleRate / lowestFrequencyHz).roundToInt() * 2
        var samples = 1024
        while (samples < required) samples *= 2
        return samples
    }

    private fun open(source: Int, sampleRate: Int): Opened {
        val analysisSamples = analysisSamples(sampleRate, Notes.getPitch(Tuner.LOWEST_NOTE))
        val minBytes = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBytes <= 0) {
            throw IllegalArgumentException("Sample rate $sampleRate Hz is not supported")
        }

        val audioRecord = AudioRecord.Builder()
            .setAudioSource(source)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBytes)
            .build()
        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release()
            throw IllegalStateException("Microphone failed to initialize at $sampleRate Hz")
        }
        try {
            audioRecord.startRecording()
        } catch (e: IllegalStateException) {
            audioRecord.release()
            throw e
        }

        return Opened(
            dispatcher = AudioDispatcher(
                AndroidMicrophoneStream(audioRecord, TarsosDSPAudioFormat(sampleRate.toFloat(), 16, 1, true, false)),
                analysisSamples,
                analysisSamples / 2
            ),
            sampleRate = sampleRate.toFloat(),
            analysisSamples = analysisSamples
        )
    }
}

/** Blocking PCM stream over an [AudioRecord]. Reads are capped to its buffer. */
private class AndroidMicrophoneStream(
    private val audioRecord: AudioRecord,
    private val format: TarsosDSPAudioFormat
) : TarsosDSPAudioInputStream {

    private val maxReadBytes = audioRecord.bufferSizeInFrames * 2

    override fun skip(bytesToSkip: Long): Long {
        throw IOException("Can not skip in audio stream")
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = audioRecord.read(b, off, minOf(len, maxReadBytes))
        if (read < 0) throw IOException("AudioRecord read failed: $read")
        return read
    }

    override fun close() {
        try {
            if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) audioRecord.stop()
        } finally {
            audioRecord.release()
        }
    }

    override fun getFormat(): TarsosDSPAudioFormat = format

    override fun getFrameLength(): Long = -1
}
