package net.doemges.kogniswarm.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.io.model.Message
import net.doemges.kogniswarm.io.model.RequestMessage
import net.doemges.kogniswarm.structure.Component
import net.doemges.kogniswarm.structure.ComponentBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InputGateway<T, R>(
    id: String,
    private val inputChannel: Channel<RequestMessage<T, R>>,
    private val inputProcessor: InputProcessor<T, R>,
    @Suppress("unused") scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : Component<InputGateway<T, R>>(id), CoroutineScope by scope {

    private val logger: Logger = LoggerFactory.getLogger(InputGateway::class.java)
    fun input(): ReceiveChannel<RequestMessage<T, R>> = inputChannel

    init {
        inputProcessor.input()
            .receiveAsFlow()
            .onEach { message ->
                logger.info("Received message: $message")
                val requestMessage = RequestMessage<T, R>(message)
                logger.info("Sending message: $requestMessage")
                inputChannel.send(requestMessage)
                logger.info("Waiting for response")
                launch {
                    val response = requestMessage.receive()
                    logger.info("Received response: $response")
                    inputProcessor.processResponse(response)
                    logger.info("Processed response")
                }

            }
            .launchIn(this)
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class InputGatewayBuilder<T, R>(id: String) : ComponentBuilder<InputGateway<T, R>>(id) {
    var inputChannel: Channel<RequestMessage<T, R>> = Channel(Channel.UNLIMITED)
    var inputProcessor: InputProcessor<T, R> = NullInputProcessor()

    override fun build(): InputGateway<T, R> = InputGateway(id, inputChannel, inputProcessor)
}

interface InputProcessor<T, R> {
    fun input(): ReceiveChannel<T>
    fun processResponse(response: Message<R>)

}

class NullInputProcessor<T, R> : InputProcessor<T, R> {
    override fun input(): ReceiveChannel<T> = Channel()
    override fun processResponse(response: Message<R>) = Unit

}