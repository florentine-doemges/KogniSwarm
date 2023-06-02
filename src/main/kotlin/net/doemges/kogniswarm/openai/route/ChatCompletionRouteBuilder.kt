package net.doemges.kogniswarm.openai.route

import net.doemges.kogniswarm.openai.processor.OpenAIChatCompletionOptimizer
import net.doemges.kogniswarm.openai.processor.OpenAIChatCompletionProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ChatCompletionRouteBuilder(
    private val openAIChatCompletionProcessor: OpenAIChatCompletionProcessor,
    private val openAIChatCompletionOptimizer: OpenAIChatCompletionOptimizer
) :
    RouteBuilder() {
    override fun configure() {
        from("direct:openai-chatcompletion")
            .wireTap("log:openai-chatcompletion.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(openAIChatCompletionOptimizer)
            .throttle(1)
            .timePeriodMillis(1_000)
            .wireTap("log:openai-chatcompletion.throttled?level=DEBUG&showAll=true&multiline=true")
            .process(openAIChatCompletionProcessor)
            .wireTap("log:openai-chatcompletion.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}