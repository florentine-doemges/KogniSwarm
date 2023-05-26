package net.doemges.kogniswarm.context

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import net.doemges.kogniswarm.core.Mission
import net.doemges.kogniswarm.openai.OpenAIChatCompletionRequest
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.springframework.stereotype.Component

@Component
class ContextProcessor(private val memoryContext: MemoryContext, private val camelContext: CamelContext) : Processor {
    @OptIn(BetaOpenAI::class)
    override fun process(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        val context = memoryContext.get(mission)

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

        exchange.getIn().headers["context"] = body.choices.first().message?.content ?: context
    }

}