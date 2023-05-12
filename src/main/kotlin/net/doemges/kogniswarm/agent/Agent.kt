package net.doemges.kogniswarm.agent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.agent.model.AgentRequest
import net.doemges.kogniswarm.agent.model.AgentResponse
import net.doemges.kogniswarm.assistant.model.AssistantRequest
import net.doemges.kogniswarm.assistant.model.AssistantResponse
import net.doemges.kogniswarm.discord.model.DiscordRequest
import net.doemges.kogniswarm.discord.model.DiscordResponse
import net.doemges.kogniswarm.io.model.Message
import net.doemges.kogniswarm.io.MessageProcessor
import net.doemges.kogniswarm.io.model.RequestMessage
import net.doemges.kogniswarm.memory.Memory
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Agent(
    val identifier: AgentIdentifier,
    private val assistant: SendChannel<RequestMessage<AssistantRequest, AssistantResponse>>,
    output: SendChannel<RequestMessage<DiscordRequest, DiscordResponse>>,
    private val memory: Memory,
    val channel: Channel<RequestMessage<AgentRequest, AgentResponse>> = Channel(),
    @Suppress("unused") scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : MessageProcessor(identifier.id.toString()), CoroutineScope by scope {

    companion object {
        const val CHANNEL_ID = "1102449789146247168"
    }

    private val logger: Logger = LoggerFactory.getLogger(Agent::class.java)

    init {
        launch { messageLoop() }
        launch { readyMessage(output) }
    }

    private suspend fun messageLoop() {
        for (message in channel) {
            logger.info("Received message: $message")
            memory.commit(message.payload.content)
            respond(message, receiveResponse(sendRequest(message)))
        }
    }

    private suspend fun readyMessage(output: SendChannel<RequestMessage<DiscordRequest, DiscordResponse>>) {
        val context = memory.getContext()
        val message = """Agent ${identifier.name} is ready.
            | My recent past is:
            | $context
        """.trimMargin()
        val payload = DiscordRequest(
            message = message,
            channelId = CHANNEL_ID
        )
        output.send(RequestMessage(payload))
        memory.commit(payload.message)
    }

    private suspend fun respond(
        message: RequestMessage<AgentRequest, AgentResponse>,
        response: Message<AssistantResponse>
    ) {
        val resp = AgentResponse("${identifier.name}: ${response.payload.response}", identifier, message.payload)
        message.respond(resp)
        memory.commit(resp.response)
        logger.info("Sent response")
    }

    private suspend fun receiveResponse(req: RequestMessage<AssistantRequest, AssistantResponse>):
        Message<AssistantResponse> {
        val response = req.receive()
        logger.info("Received response: $response")
        return response
    }

    private suspend fun sendRequest(message: RequestMessage<AgentRequest, AgentResponse>):
        RequestMessage<AssistantRequest, AssistantResponse> {
        val req = RequestMessage<AssistantRequest, AssistantResponse>(
            AssistantRequest(
                message.payload.content,
                "Your name is '${identifier.name}'. Let me remind you of your recent past: ${memory.getContext()}"
            )
        )
        logger.info("Sending request: $req")
        assistant.send(req)
        logger.info("Sent request")
        return req
    }

}