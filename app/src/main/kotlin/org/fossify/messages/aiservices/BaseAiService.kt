package org.fossify.messages.aiservices

import android.content.Context
import org.fossify.messages.extensions.config
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.serialization.kotlinx.json.json

abstract class BaseAiService(private var context: Context) {
    protected val client = HttpClient(CIO) {
        engine {
            requestTimeout = 100_000
        }
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(context.config.aiApiKey, "")
                }
            }
        }
    }
    protected val testPrompt = "Your role is to read the SMS/MMS sentence and evaluate in one line whether it is smishing or spam. Your response must be in the same language as the given text. Text: "

    abstract suspend fun generateText(promptText: String): String?

}
