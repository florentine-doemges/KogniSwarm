package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.openai.OpenAIChatCompletionProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ChatCompletionRouteBuilder(private val openAIChatCompletionProcessor: OpenAIChatCompletionProcessor) :
    RouteBuilder() {
    override fun configure() {
        from("direct:openai-chatcompletion")
            .wireTap("log:openai-chatcompletion.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(openAIChatCompletionProcessor)
            .wireTap("log:openai-chatcompletion.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}