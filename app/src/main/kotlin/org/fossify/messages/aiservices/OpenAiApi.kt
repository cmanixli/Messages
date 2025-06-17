package org.fossify.messages.aiservices

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import android.content.Context
import org.fossify.messages.extensions.config
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.fossify.commons.helpers.mydebug

class OpenAiApi(private var context: Context) : BaseAiService(context) {
    override val client = super.client.config {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(context.config.aiApiKey, "")
                }
            }
        }
    }
    @Serializable
    data class Content (
        val type: String,
        val text: String,
    )
    @Serializable
    data class Output (
        val type: String,
        val id: String,
        val status: String,
        val role: String,
        val content: Content,
    )
    @Serializable
    @JsonIgnoreUnknownKeys
    data class Response (
        val id: String,
        val created_at: Double,
        var status: String,
        var error: String?,
        var model: String,
        var output: Output,
    )
    override suspend fun generateText(message: String): String? {
        return try {
            val httpResponse: HttpResponse = client.post(config.aiApiUrl + "/v1/responses") {
                contentType(ContentType.Application.Json)
                setBody(Request(
                    config.aiApiModel,
                    config.aiPrompt + message,
                    false
                ))
            }

            if (httpResponse.status == HttpStatusCode.OK) {
                val response = httpResponse.body<Response>()
                "\n\n[AI Report]\n" + response.output.content.text
            } else {
                mydebug("OpenAI API Error: ${httpResponse.status} - ${httpResponse.bodyAsText()}")
                null
            }
        } catch (e: Exception) {
            mydebug("Exception during OpenAiApi call: ${e.message}")
            null
        }
    }
}
