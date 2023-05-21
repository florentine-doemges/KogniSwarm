package net.doemges.kogniswarm.core

import net.doemges.kogniswarm.openai.OpenAIChatCompletionProcessor
import net.doemges.kogniswarm.openai.OpenAIModelProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KogniSwarmRouteBuilder(
    @Value("\${discord.channelId.default}") private val channelId: String,
    private val openAIModelProcessor: OpenAIModelProcessor,
    private val openAIChatCompletionProcessor: OpenAIChatCompletionProcessor
) : RouteBuilder() {
    override fun configure() {
        from("jda://discord?channelId=$channelId")
            .to("log:discord.incoming?level=INFO&showAll=true&multiline=true")

        from("direct:discord")
            .log("log:discord.outgoing?level=INFO&showAll=true&multiline=true")
            .to("jda://discord?channelId=$channelId")

        from("direct:openai-models")
            .log("log:openai-models.incoming?level=INFO&showAll=true&multiline=true")
            .process(openAIModelProcessor)
            .log("log:openai-models.outgoing?level=INFO&showAll=true&multiline=true")

        from("direct:openai-chatcompletion")
            .log("log:openai-chatcompletion.incoming?level=INFO&showAll=true&multiline=true")
            .process(openAIChatCompletionProcessor)
            .log("log:openai-chatcompletion.outgoing?level=INFO&showAll=true&multiline=true")

    }
}


