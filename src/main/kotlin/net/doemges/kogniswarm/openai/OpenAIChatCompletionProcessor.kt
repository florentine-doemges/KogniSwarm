package net.doemges.kogniswarm.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OpenAIChatCompletionProcessor(
    private val openAI: OpenAI,
    private val objectMapper: ObjectMapper
) : CoroutineAsyncProcessor() {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    override suspend fun processSuspend(exchange: Exchange) {
        (exchange.getIn().body as OpenAIChatCompletionRequest)
            .also { openAIChatCompletionRequest ->
                logger.debug("Chat Completion Request: $openAIChatCompletionRequest")
                openAIChatCompletionRequest.messages?.forEach {
                    logger.info("Chat Completion Request Message: ${it.content} (${it.role?.role ?: "unknown"})")
                }
                val chatCompletionRequest = openAIChatCompletionRequest.asChatCompletionRequest(openAI)
                logger.debug(objectMapper.writeValueAsString(chatCompletionRequest))
                openAI
                    .chatCompletion(chatCompletionRequest)
                    .also { chatCompletion ->
                        logger.info("Chat Completion: ${chatCompletion.choices[0].message?.content}")
                        exchange.message.body = chatCompletion
                    }

            }

    }
}

