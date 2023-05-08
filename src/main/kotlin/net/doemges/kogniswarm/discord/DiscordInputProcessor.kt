package net.doemges.kogniswarm.discord

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.io.InputProcessor
import net.doemges.kogniswarm.io.Message
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.log

class DiscordInputProcessor(jda: JDA, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) :
    InputProcessor<DiscordRequest, DiscordResponse>, EventListener, CoroutineScope by scope {

    init {
        jda.addEventListener(this)
    }

    private val channel = Channel<DiscordRequest>(capacity = Channel.UNLIMITED, onUndeliveredElement = {
        logger.error("Channel is full, dropping element: $it")
    })

    private val logger: Logger = LoggerFactory.getLogger(DiscordInputProcessor::class.java)
    override fun input(): ReceiveChannel<DiscordRequest> = channel
    override fun processResponse(response: Message<DiscordResponse>) {
        val request = response.payload.request
        logger.info("Responding to request: $request")
        launch {
            request.event?.channel?.sendMessage(response.payload.message)
                ?.queue()
            logger.info("Sent response")
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onEvent(event: GenericEvent) {
        logger.info("Received event: $event")
        if (event !is MessageReceivedEvent) return
        logger.info("Received message: ${event.message.contentRaw}")
        launch {
            logger.info("Sending request")
            val discordRequest = DiscordRequest(event)
            channel.send(discordRequest)
            logger.info("Sent request: $discordRequest")
            logger.info(
                DebugProbes.dumpCoroutinesInfo()
                    .joinToString("\n")
            )
        }
    }
}
