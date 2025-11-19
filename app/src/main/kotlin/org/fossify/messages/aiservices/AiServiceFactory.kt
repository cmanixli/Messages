package org.fossify.messages.aiservices

import android.content.Context
import org.fossify.messages.extensions.config
import org.fossify.messages.helpers.AiService.*
//import org.fossify.messages.helpers.*

object AiServiceFactory {
    fun create(context: Context): BaseAiService {
        when (context.config.aiApiService) {
            OLLAMA.ordinal -> return OllamaApi(context)
            OPENAI.ordinal -> return OpenAiApi(context)
            GEMINI.ordinal -> return GeminiApi(context)
            ONDEVICE.ordinal -> return OnDeviceApi(context)
            else -> {
                return AiDisabled(context)
            }
        }
    }
}
