package org.fossify.messages.aiservices

import android.content.Context
import org.fossify.messages.extensions.config
import org.fossify.messages.helpers.*

object AiServiceFactory {
    fun create(context: Context): BaseAiService {
        when (context.config.aiApiService) {
            OPENAI -> return OpenAiApi(context)
            else -> {
                return OllamaApi(context)
            }
        }
    }
}
