package net.doemges.kogniswarm.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.Gateway
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import org.springframework.stereotype.Service

@OptIn(PrivilegedIntent::class)
@Service
class DiscordService(
    private val kord: Kord,
    val discordEventChannel: Channel<Request<EventWrapper>>
) {

    private final val scope = CoroutineScope(Dispatchers.IO)

    private val channelId: Snowflake = Snowflake(1102449789146247168)

    init {
        scope.launch {
            kord.events.filterIsInstance<MessageCreateEvent>()
                    .collect { messageCreateEvent ->
                        val responseChannel: Channel<Response<EventWrapper>> = Channel()
                        discordEventChannel.send(Request(EventWrapper(messageCreateEvent), responseChannel))
                        val response = responseChannel.receive()
                        response.message.reaction?.also {
                            messageCreateEvent.message.channel.createMessage(it.message)
                        }
                    }
            kord.login {
                intents = Intents.nonPrivileged + Intent.MessageContent
            }
        }
    }

    suspend fun sendMessage(message: String) {
        kord.getChannelOf<MessageChannel>(channelId)
                ?.createMessage(message)

    }

}

