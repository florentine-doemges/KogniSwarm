package net.doemges.kogniswarm.core.processor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import net.doemges.kogniswarm.config.SerializationConfig
import net.doemges.kogniswarm.core.Message
import net.doemges.kogniswarm.core.consumer.Consumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class BaseProcessor(
    override var input: Flow<Message<*>>? = null,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : Processor, CoroutineScope by scope {

    protected val channel: Channel<Message<*>> = Channel<Message<*>>(Channel.UNLIMITED).apply {
        receiveAsFlow().onEach { message -> logger.info("Sending message... $message") }
    }

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun start() {
        logger.info("Starting processor ${this.javaClass.simpleName}")
        launch {
            input
                ?.onEach { message -> processMessage(message) }
                ?.collect()
        }
    }

    protected open suspend fun processMessage(message: Message<*>) {
        logger.info("Received message $message")
    }

    override fun output(): Flow<Message<*>> = channel.receiveAsFlow()
}
