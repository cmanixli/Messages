package org.fossify.messages.aiservices

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import android.content.Context
import org.fossify.messages.extensions.config
import io.ktor.client.call.body
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.fossify.commons.helpers.mydebug

class OllamaApi(private var context: Context) : BaseAiService(context) {
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class Response (
        var model: String,
        var remote_model: String,
        var remote_host: String,
        var created_at: String,
        var response: String,
        var done: Boolean,
        var done_reason: String,
        var total_duration: Double,
        var prompt_eval_count: Int,
        var eval_count: Int,
    )

    override suspend fun generateText(message: String): String? {
        return try {
            val httpResponse: HttpResponse = client.post(config.aiApiUrl + "/api/generate") {
                contentType(ContentType.Application.Json)
                setBody(Request(
                    config.aiApiModel,
                    config.aiPrompt + message,
                    false
                ))
            }

            if (httpResponse.status == HttpStatusCode.OK) {
                val response: Response = Json.decodeFromString(httpResponse.bodyAsText())
                // val response = httpResponse.body<Response>()
                "[AI Report]\n" + response.response
            } else {
                mydebug("Ollama API Error: ${httpResponse.status} - ${httpResponse.bodyAsText()}")
                null
            }
        } catch (e: Exception) {
            mydebug("Exception during Ollama call: ${e.message}")
            null
        }
    }
}




