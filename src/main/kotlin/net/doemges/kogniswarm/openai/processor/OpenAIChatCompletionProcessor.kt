package net.doemges.kogniswarm.openai.processor

import com.aallam.openai.api.BetaOpenAI
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.openai.model.OpenAIChatCompletionRequest
import net.doemges.kogniswarm.openai.service.OpenAIChatCompletionService
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OpenAIChatCompletionProcessor(
    private val openAIChatCompletionService: OpenAIChatCompletionService
) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    override fun process(exchange: Exchange) {
        (exchange.getIn().body as OpenAIChatCompletionRequest).also { openAIChatCompletionRequest ->
            logger.debug("Chat Completion Request: {}", openAIChatCompletionRequest)
            openAIChatCompletionRequest.messages?.forEach {
                logger.info("Chat Completion Request Message:\n${it.content} (${it.role?.role ?: "unknown"})")
            }
            val chatCompletion =
                runBlocking { openAIChatCompletionService.getChatCompletion(openAIChatCompletionRequest) }
            logger.info("Chat Completion: ${chatCompletion.choices[0].message?.content}")
            exchange.message.body = chatCompletion
        }
    }
}
