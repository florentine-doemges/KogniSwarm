package net.doemges.kogniswarm.discord

import kotlinx.coroutines.launch
import net.doemges.kogniswarm.core.producer.BaseProducer
import net.doemges.kogniswarm.core.producer.BaseProducerBuilder
import net.doemges.kogniswarm.core.Message
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DiscordProducer(
    token: String
) : BaseProducer(), EventListener {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val jda: JDA = JDABuilder.createDefault(token)
        .build()

    init {
        jda.awaitReady()
            .apply { addEventListener(this@DiscordProducer) }
    }

    companion object {
        fun builder(block: Builder.() -> Unit = {}): Builder =
            Builder().apply(block)
    }

    class Builder : BaseProducerBuilder<DiscordProducer>() {
        private var token: String? = null

        fun token(token: String): Builder = apply { this.token = token }
        override fun build(): DiscordProducer = DiscordProducer(token = token ?: error("discord bot token must be set"))
    }

    override fun onEvent(event: GenericEvent) = logger.info("Received event $event")
        .also {
            when (event) {
                is MessageReceivedEvent -> onMessageReceived(event)
                is MessageUpdateEvent -> onMessageUpdate(event)
                else -> return
            }
        }


    private fun onMessage(message: net.dv8tion.jda.api.entities.Message) {
        logger.info("Received message ${message.javaClass.simpleName} ${message.contentRaw}")
        launch {
            logger.info("Sending message ${message.contentRaw}")
            channel.send(Message(message))
            logger.info("Sent message ${message.contentRaw}")
        }
    }

    private fun onMessageReceived(event: MessageReceivedEvent) = onMessage(event.message)
    private fun onMessageUpdate(event: MessageUpdateEvent) = onMessage(event.message)


}