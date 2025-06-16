package org.fossify.messages.aiservices

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import android.content.Context
import org.fossify.messages.extensions.config
import io.ktor.client.call.body
import kotlinx.serialization.Serializable
import org.fossify.commons.helpers.mydebug

class OllamaApi(private var context: Context) : BaseAiService(context) {
    private var config = context.config
    override suspend fun generateText(promptText: String): String? {
        return try {
            val httpResponse: HttpResponse = client.post(config.aiApiUrl + "/api/generate") {
                contentType(ContentType.Application.Json)
                setBody(OllamaRequest(
                    config.aiApiModel,
                    config.aiPrompt + promptText,
                    false
                ))
            }

            if (httpResponse.status == HttpStatusCode.OK) {
                val response = httpResponse.body<OllamaResponse>()
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

@Serializable
data class OllamaRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean
)

@Serializable
data class OllamaResponse(
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
