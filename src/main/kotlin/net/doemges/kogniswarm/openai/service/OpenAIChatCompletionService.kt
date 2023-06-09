package net.doemges.kogniswarm.openai.service

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.openai.model.OpenAIChatCompletionRequest
import net.doemges.kogniswarm.openai.model.OpenAIChatMessage
import net.doemges.kogniswarm.openai.model.OpenAIMessagesBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@OptIn(BetaOpenAI::class)
class OpenAIChatCompletionService(
    private val openAI: OpenAI, private val objectMapper: ObjectMapper
) {
    private val defaultModel = "gpt-3.5"

    private val logger = LoggerFactory.getLogger(javaClass)

    private fun buildChatCompletionRequest(block: OpenAIMessagesBuilder.() -> Unit) =
        OpenAIChatCompletionRequest.builder {
            modelRequest {
                modelName(defaultModel)
            }
            messages(block)
        }
            .build()

    suspend fun getChatCompletion(openAIChatCompletionRequest: OpenAIChatCompletionRequest): ChatCompletion =
        openAIChatCompletionRequest.asChatCompletionRequest(openAI)
            .let { chatCompletionRequest ->
                logger.debug(objectMapper.writeValueAsString(chatCompletionRequest))
                openAI.chatCompletion(chatCompletionRequest)
            }

    private suspend fun runChatCompletion(block: OpenAIMessagesBuilder.() -> Unit): ChatCompletion =
        getChatCompletion(buildChatCompletionRequest(block))

    val simpleChatCompletion: suspend (String) -> String = { input -> completeString(input) }

    suspend fun completeMessages(block: OpenAIMessagesBuilder.() -> Unit = {}): ChatCompletion = runChatCompletion(block)

    suspend fun completeMessage(block: OpenAIChatMessage.Builder.() -> Unit = {}): ChatCompletion =
        completeMessages { message(block) }

    suspend fun completeString(input: String): String =
        completeMessage { content(input) }.choices.first().message?.content ?: error("no completion found")
}
