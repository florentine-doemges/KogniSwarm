package net.doemges.kogniswarm.agent

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefaultDiscordEventDispatcher(
    private val kord: Kord,
    private val agent: Agent,
    private val commandQueue: CommandQueue
) : DiscordEventDispatcher {
    private lateinit var id: AgentIdentifier

    init {
        kord.events.filterIsInstance<MessageCreateEvent>()
                .onEach { commandQueue.processMessageEvent(it) }
                .launchIn(agent)
    }

    override fun setId(id: AgentIdentifier) {
        this.id = id
    }
}