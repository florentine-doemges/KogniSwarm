package net.doemges.kogniswarm.discord

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.slf4j.LoggerFactory

class MessageListener(private val discordEventChannel: Channel<Request<EventWrapper>>) : EventListener {

    private val logger = LoggerFactory.getLogger(MessageListener::class.java)
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onEvent(event: GenericEvent) {
        logger.info("Event received: ${event.javaClass.simpleName}")
        if (event is MessageReceivedEvent) {
            logger.info("Message received: ${event.message.contentRaw}")
            scope.launch {
                val responseChannel: Channel<Response<EventWrapper>> = Channel()
                discordEventChannel.send(Request(EventWrapper(event), responseChannel))
                val response = responseChannel.receive()
                response.message.reaction?.also {
                    event.channel.sendMessage(it.message)
                            .queue()
                }
            }
        }
    }
}

