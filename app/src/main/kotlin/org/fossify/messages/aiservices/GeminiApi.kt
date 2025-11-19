package org.fossify.messages.aiservices

import android.content.Context
import com.google.genai.Client
import com.google.genai.types.GenerateContentResponse
import org.fossify.commons.helpers.mydebug
import org.fossify.messages.extensions.config

class GeminiApi(private var context: Context) : BaseAiService(context) {
    override suspend fun generateText(message: String): String? {
        return try {
            val client = Client.builder().apiKey(context.config.aiApiKey).build()
            val response: GenerateContentResponse = client.models.generateContent(
                context.config.aiApiModel,
                config.aiPrompt + message,
                null
            )
            response.text()
        }
        catch (e: Exception) {
            mydebug("Exception during OpenAiApi call: ${e.message}")
            null
        }
    }
}
