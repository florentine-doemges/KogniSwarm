package net.doemges.kogniswarm.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.io.model.RequestMessage
import net.doemges.kogniswarm.structure.Component
import net.doemges.kogniswarm.structure.ComponentBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OutputGateway<T, R>(
    id: String,
    private val outputChannel: Channel<RequestMessage<T, R>>,
    private val outputProcessor: OutputProcessor<T, R>,
    @Suppress("unused") scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : Component<OutputGateway<T, R>>(id), CoroutineScope by scope {

    private val logger: Logger = LoggerFactory.getLogger(OutputGateway::class.java)
    fun output(): SendChannel<RequestMessage<T, R>> = outputChannel

    init {
        launch {
            for (message in outputChannel) {
                logger.info("Received message: $message")
                val response: R = outputProcessor.processRequest(message.payload)
                logger.info("Sending response: $response")
                message.respond(response)
                logger.info("Sent response")
            }
        }
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class OutputGatewayBuilder<T, R>(id: String) : ComponentBuilder<OutputGateway<T, R>>(id) {
    var outputChannel: Channel<RequestMessage<T, R>> = Channel(Channel.UNLIMITED)
    var outputProcessor: OutputProcessor<T, R> = NullOutputProcessor()
    override fun build(): OutputGateway<T, R> = OutputGateway(id, outputChannel, outputProcessor)
}

class NullOutputProcessor<T, R> : OutputProcessor<T, R> {
    override fun processRequest(payload: T): R = TODO("Not yet implemented")
}

interface OutputProcessor<T, R> {
    fun processRequest(payload: T): R
}