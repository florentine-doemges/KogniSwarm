package net.doemges.kogniswarm.io.model

import kotlinx.coroutines.channels.Channel

class RequestMessage<T, R>(
    payload: T,
    headers: Map<String, Any> = emptyMap(),
    private val responseChannel: Channel<Message<R>> = Channel()
) :
    Message<T>(payload, headers) {
    suspend fun respond(response: R) {
        responseChannel.send(Message(response, headers))
    }

    suspend fun receive(): Message<R> = responseChannel.receive()

    override fun toString(): String =
        "RequestMessage(payload=$payload, headers=$headers, responseChannel=$responseChannel)"
}