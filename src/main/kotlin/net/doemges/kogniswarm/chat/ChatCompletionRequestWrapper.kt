package net.doemges.kogniswarm.chat

import net.doemges.kogniswarm.chat.model.ChatCompletionRequest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

class ChatCompletionRequestWrapper(request: ChatCompletionRequest, apiKey: String) :
    HttpEntity<ChatCompletionRequest>(request, createHeaders(apiKey)) {

    companion object {
        private fun createHeaders(apiKey: String): HttpHeaders {
            val headers = HttpHeaders()
            headers.add("Authorization", "Bearer $apiKey")
            headers.add("Content-Type", "application/json")
            return headers
        }
    }
}
