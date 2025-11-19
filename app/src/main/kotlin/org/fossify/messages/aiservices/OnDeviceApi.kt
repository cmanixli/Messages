package org.fossify.messages.aiservices

import android.content.Context
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import org.fossify.commons.helpers.mydebug


class OnDeviceApi(private var context: Context) : BaseAiService(context) {
    override suspend fun generateText(message: String): String? {
        return try {
            val generativeModel = downloadModel()
            val response = generativeModel.generateContent(config.aiPrompt + message)
            response.toString()
        }
        catch (e: Exception) {
            mydebug("Exception during OnDeviceAI call: ${e.message}")
            null
        }
    }

    suspend fun downloadModel(): GenerativeModel {
        val generativeModel = Generation.getClient()
        val status = generativeModel.checkStatus()
        when (status) {
            FeatureStatus.UNAVAILABLE -> {
                mydebug("Gemini Nano not supported on this device or device hasn't fetched the latest configuration to support it")
            }

            FeatureStatus.DOWNLOADABLE -> {
                mydebug("Gemini Nano can be downloaded on this device, but is not currently downloaded")
                generativeModel.download().collect { status ->
                    when (status) {
                        is DownloadStatus.DownloadStarted ->
                            mydebug("starting download for Gemini Nano")

                        is DownloadStatus.DownloadProgress ->
                            mydebug("Nano ${status.totalBytesDownloaded} bytes downloaded")

                        DownloadStatus.DownloadCompleted -> {
                            mydebug("Gemini Nano download complete")
                        }

                        is DownloadStatus.DownloadFailed -> {
                            mydebug("Nano download failed ${status.e.message}")
                        }
                    }
                }
            }

            FeatureStatus.DOWNLOADING -> {
                mydebug("Gemini Nano currently being downloaded")
            }

            FeatureStatus.AVAILABLE -> {
                mydebug("Gemini Nano currently downloaded and available to use on this device")
            }
        }
        return generativeModel
    }
}
