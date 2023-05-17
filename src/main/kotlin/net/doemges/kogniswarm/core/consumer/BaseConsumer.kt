package net.doemges.kogniswarm.core.consumer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.core.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class BaseConsumer(
    override var input: Flow<Message<*>>? = null,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : Consumer, CoroutineScope by scope {

    override fun start() {
        logger.info("Starting consumer ${this.javaClass.simpleName}")
        launch {
            input
                ?.onEach { message -> processMessage(message) }
                ?.collect()
        }
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
    protected open fun processMessage(message: Message<*>) {
        logger.info("Received message $message")
    }

}