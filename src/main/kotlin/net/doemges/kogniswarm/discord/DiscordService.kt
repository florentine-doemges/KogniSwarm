package net.doemges.kogniswarm.discord

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.io.Request
import net.dv8tion.jda.api.JDA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class DiscordService(
    private val jda: JDA,
    val discordEventChannel: Channel<Request<EventWrapper>>
) {

    private val logger: Logger = LoggerFactory.getLogger(DiscordService::class.java)

    private final val scope = CoroutineScope(Dispatchers.IO)

    private val channelId: Long = 1102449789146247168

    val ready = AtomicBoolean(false)

    init {
        logger.info("Initializing DiscordService")
        jda.addEventListener(MessageListener(discordEventChannel))

        scope.launch {
            jda.awaitReady()
            ready.set(true)
            logger.info("JDA is ready")
        }
    }

    suspend fun sendMessage(message: String) {
        val textChannel = jda.getTextChannelById(channelId)
        val sendMessage = textChannel?.sendMessage(message)
        sendMessage?.queue{
            logger.info("Message sent")
        }
    }

}