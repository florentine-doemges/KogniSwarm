package net.doemges.kogniswarm.agent

import dev.kord.core.event.message.MessageCreateEvent

interface CommandQueue {
    suspend fun processMessageEvent(event: MessageCreateEvent)
    fun getNextMessageTask(): MessageTask?
    suspend fun getNextCommand(): String
}