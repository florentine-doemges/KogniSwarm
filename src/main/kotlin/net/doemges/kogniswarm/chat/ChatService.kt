package net.doemges.kogniswarm.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import net.doemges.kogniswarm.chat.model.ChatCompletionRequest
import net.doemges.kogniswarm.chat.model.ChatCompletionResponse
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@Service
class ChatService @Autowired constructor(
    private val restTemplate: RestTemplate,
    private val chatCompletionRequestFactory: ChatCompletionRequestFactory,
    @Value("\${openai.api.key}") private val apiKey: String? = null
) {


    private val scope = CoroutineScope(Dispatchers.IO)

    fun sendToChatGpt(input: ChatMessageBundle): String {
        val request = chatCompletionRequestFactory.createChatCompletionRequest(input)
        val response: ChatCompletionResponse = createCompletion(request)
        return response.choices[0].message.content
    }

    fun sendToChatGpt(input: ReceiveChannel<ChatMessageBundle>): ReceiveChannel<String> = scope.produce {
        for (chatMessageBundle in input) {
            send(sendToChatGpt(chatMessageBundle))
        }
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
