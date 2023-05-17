package net.doemges.kogniswarm.core.producer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import net.doemges.kogniswarm.core.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class BaseProducer(
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : Producer, CoroutineScope by scope {

    protected val channel: Channel<Message<*>> = Channel<Message<*>>(Channel.UNLIMITED).apply {
        receiveAsFlow().onEach { message -> logger.info("Sending message $message") }
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override fun output(): Flow<Message<*>> = channel.receiveAsFlow()

    override fun start() {
        logger.info("Starting producer ${this.javaClass.simpleName}")
    }
}