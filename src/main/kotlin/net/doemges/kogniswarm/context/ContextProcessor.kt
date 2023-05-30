package net.doemges.kogniswarm.context

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import net.doemges.kogniswarm.core.Mission
import net.doemges.kogniswarm.openai.OpenAIChatCompletionRequest
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ContextProcessor(
    private val memoryContext: MemoryContext,
    private val camelContext: CamelContext
) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    override fun process(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        val context = memoryContext.get(mission, 10)

        logger.info("Pre Context: $context")

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
            }
            .build()

        val body = camelContext.createProducerTemplate()
            .requestBody("direct:openai-chatcompletion", msg) as ChatCompletion

        val postContext = body.choices.first().message?.content ?: context
        logger.info("Post Context: $postContext")
        exchange.getIn().headers["context"] = postContext
    }

}