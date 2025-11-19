package org.fossify.messages.aiservices

import android.content.Context

class AiDisabled(private val context: Context) : BaseAiService(context){
    override suspend fun generateText(message: String): String? {
        return null
    }
}
