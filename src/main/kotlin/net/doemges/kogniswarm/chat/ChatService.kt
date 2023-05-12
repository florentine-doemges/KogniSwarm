package net.doemges.kogniswarm.chat

import net.doemges.kogniswarm.chat.model.ChatCompletionRequest
import net.doemges.kogniswarm.chat.model.ChatCompletionResponse
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.IOException

@Service
class ChatService @Autowired constructor(
    private val restTemplate: RestTemplate,
    private val chatCompletionRequestFactory: ChatCompletionRequestFactory,
    @Value("\${openai.api.key}") private val apiKey: String? = null
) {
    private val logger : Logger = LoggerFactory.getLogger(ChatService::class.java)

    fun sendToChatGpt(input: ChatMessageBundle): String {
        logger.info("Request: $input")
        val request = chatCompletionRequestFactory.createChatCompletionRequest(input)
        val response: ChatCompletionResponse = createCompletion(request)
        val out = response.choices[0].message.content
        logger.info("Response $out")
        return out
    }

    private fun createCompletion(request: ChatCompletionRequest): ChatCompletionResponse {
        val requestWrapper = ChatCompletionRequestWrapper(request, apiKey!!)
        val responseEntity: ResponseEntity<ChatCompletionResponse> = restTemplate.exchange(
            "https://api.openai.com/v1/chat/completions",
            HttpMethod.POST,
            requestWrapper,
            ChatCompletionResponse::class.java
        )

        if (!responseEntity.statusCode.is2xxSuccessful) {
            throw IOException("Unexpected response: $responseEntity")
        }

        if (responseEntity.body == null) {
            throw IOException("The response body is null: $responseEntity")
        }

        return responseEntity.body!!
    }
}
