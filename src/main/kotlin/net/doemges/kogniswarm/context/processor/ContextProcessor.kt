package net.doemges.kogniswarm.context.processor

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import net.doemges.kogniswarm.context.service.MemoryContextService
import net.doemges.kogniswarm.mission.model.MissionKey
import net.doemges.kogniswarm.openai.model.OpenAIChatCompletionRequest
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class ContextProcessor(
    private val memoryContextService: MemoryContextService,
    private val camelContext: CamelContext
) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    override fun process(exchange: Exchange) {
        val missionKey = exchange.getIn().body as MissionKey
        val contextList = memoryContextService.get(
            missionKey = missionKey,
            limit = 10
        )

        if (contextList.isEmpty()) {
            logger.debug("No context found for mission $missionKey")
            return
        }

        val context = memoryContextService.formatContext(contextList, 1_000)

        logger.debug("Pre Context: $context")

        val msg = OpenAIChatCompletionRequest
            .builder {
                modelRequest {
                    modelName("gpt")
                    isGPT4allowed(false)
                }
                messages {
                    message {
                        content(context)
                        role(ChatRole.User)
                    }
                    message {
                        content("Write a summary.")
                        role(ChatRole.System)
                    }
                }
                maxTokens(1000)
            }
            .build()

        val body = camelContext.createProducerTemplate()
            .requestBody("direct:openai-chatcompletion", msg) as ChatCompletion

        val postContext = body.choices.first().message?.content ?: context
        logger.debug("Post Context: $postContext")
        exchange.getIn().headers["context"] = postContext
    }

}