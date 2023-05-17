package net.doemges.kogniswarm.discord

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.agent.AgentResponse
import net.doemges.kogniswarm.core.consumer.BaseConsumer
import net.doemges.kogniswarm.core.consumer.BaseConsumerBuilder
import net.doemges.kogniswarm.core.Message
import org.slf4j.LoggerFactory

class DiscordConsumer(input: Flow<Message<*>>?) : BaseConsumer(input) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        fun builder(block: Builder.() -> Unit = {}): Builder =
            Builder().apply(block)
    }

    class Builder : BaseConsumerBuilder<DiscordConsumer>() {
        override fun build(): DiscordConsumer = DiscordConsumer(input)
    }

    override fun processMessage(message: Message<*>) {
        super.processMessage(message)
        val payload = message.payload as AgentResponse
        val response = payload.response
        val msg = payload.message
        val channel = msg.channel
        logger.info("Sending message $response to ${channel.name}")
        channel.sendMessage(response)
            .queue()
    }
}