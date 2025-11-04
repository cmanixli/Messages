package org.fossify.messages.aiservices

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import android.content.Context
import org.fossify.messages.extensions.config
import io.ktor.client.call.body
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.fossify.commons.helpers.mydebug

class OllamaApi(private var context: Context) : BaseAiService(context) {
    @Serializable
    @JsonIgnoreUnknownKeys
    data class Response (
        val model: String,
        val created_at: String,
        val response: String,
        var done: Boolean,
        var done_reason: String,
        var context: ArrayList<Int>,
        var total_duration: Double,
        var load_duration: Double,
        var prompt_eval_count: Int,
        var prompt_eval_duration: Double,
        var eval_count: Int,
        var eval_duration: Double
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
                val response = httpResponse.body<Response>()
                "\n\n[AI Report]\n" + response.response
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




