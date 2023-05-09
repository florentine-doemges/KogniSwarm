package net.doemges.kogniswarm.agent

import io.micrometer.core.annotation.Timed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import net.doemges.kogniswarm.discord.DiscordRequest
import net.doemges.kogniswarm.discord.DiscordResponse
import net.doemges.kogniswarm.io.MessageProcessor
import net.doemges.kogniswarm.io.RequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class AgentMessageProcessor(
    id: String,
    val input: ReceiveChannel<RequestMessage<DiscordRequest, DiscordResponse>>,
    private val agentManager: AgentManager,
    @Suppress("unused") scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : MessageProcessor(id), CoroutineScope by scope {

    private val logger: Logger = LoggerFactory.getLogger(AgentMessageProcessor::class.java)

    init {
        input.receiveAsFlow()
            .onEach { message -> processMessage(message) }
            .launchIn(this)

    }

    @Timed(
        value = "agent.message.processed",
        description = "Agent message processing time and count",
        extraTags = ["type", "counter"]
    )
    private suspend fun processMessage(message: RequestMessage<DiscordRequest, DiscordResponse>) {
        logger.info("New message received in AgentMessageProcessor: $message")
        message.payload.event?.message?.contentRaw?.let {
            logger.info("Received message: $it")
            if (it.startsWith("@")) {
                val name = it.substringBefore(" ")
                    .substringAfter("@")
                logger.info("Name: $name")
                val content = it.substringAfter(" ")
                logger.info("Content: $content")
                agentManager.getByNameOrNull(name)
                    ?.also { agent ->
                        logger.info("Agent: $agent")
                        val channel = agent.channel
                        val req = RequestMessage<AgentRequest, AgentResponse>(AgentRequest(content))
                        logger.info("Sending request: $req")
                        channel.send(req)
                        logger.info("Sent request")
                        val res = req.receive()
                        logger.info("Received response: $res")
                        message.respond(DiscordResponse(res.payload.response, message.payload))
                        logger.info("Sent response")
                    }
            } else {
                logger.info("Message does not start with @: $it")
            }

        }
    }
}

