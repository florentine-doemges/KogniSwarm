package net.doemges.kogniswarm.agent

import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory

class Agent(
    val scope: CoroutineScope,
    val id: AgentIdentifier,
    val kord: Kord
) : CoroutineScope by scope {

    private val logger = LoggerFactory.getLogger(Agent::class.java)

    val messageQueue: MutableList<Message> = mutableListOf()

    init {
        kord.events.onEach { event ->
            logger.info("Received event: $event")
            if (event is MessageCreateEvent) {
                messageQueue.add(event.message)
            }
        }.launchIn(this)
    }

    override fun toString(): String = "Agent(id=$id)"

}
