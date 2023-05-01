package net.doemges.kogniswarm.agent

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.runBlocking
import org.springframework.shell.ResultHandler

class MessageTask(val event: MessageCreateEvent, private val kord: Kord) : ResultHandler<Any> {
    override fun handleResult(result: Any?) {
        runBlocking {
            event.message.channel.createMessage("$result")
        }
    }
}