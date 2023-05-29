package net.doemges.kogniswarm.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.client.OpenAI
import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OpenAIChatCompletionProcessor(
    private val openAI: OpenAI
) : CoroutineAsyncProcessor() {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    override suspend fun processSuspend(exchange: Exchange) {
        (exchange.getIn().body as OpenAIChatCompletionRequest)
            .also { openAIChatCompletionRequest ->
                logger.info("Chat Completion Request: $openAIChatCompletionRequest")
                openAI
                    .chatCompletion(openAIChatCompletionRequest.asChatCompletionRequest(openAI))
                    .also { chatCompletion ->
                        logger.info("Chat Completion: ${chatCompletion.choices[0].message?.content}")
                        exchange.message.body = chatCompletion
                    }

            }

    }
}

