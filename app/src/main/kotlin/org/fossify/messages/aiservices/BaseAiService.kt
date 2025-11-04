package org.fossify.messages.aiservices

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import org.fossify.messages.extensions.config

abstract class BaseAiService(private val context: Context) {
    @Serializable
    data class Request (
        val model: String,
        val prompt: String,
        val stream: Boolean,
    )
    open val client = HttpClient(CIO) {
        engine {
            requestTimeout = 100_000
        }
        install(ContentNegotiation) {
            json()
        }
    }
    protected var config = context.config

    abstract suspend fun generateText(message: String): String?
}
