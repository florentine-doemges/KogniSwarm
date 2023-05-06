package net.doemges.kogniswarm.agent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.discord.DiscordService
import net.doemges.kogniswarm.discord.EventWrapper
import net.doemges.kogniswarm.discord.Reaction
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Suppress("SameParameterValue", "SameParameterValue")
@Component
class DiscordHandler(
    discordService: DiscordService,
    private val agentService: AgentService
) {
    private val logger: Logger = LoggerFactory.getLogger(DiscordHandler::class.java)

    private final val scope = CoroutineScope(Dispatchers.IO)

    private val filteredChannel = discordService.discordEventChannel.receiveAsFlow()
        .filter { req -> agentService.agentNames.any { req.message.event.message.contentRaw.startsWith("@$it ") } }
        .onEach { logger.info("Message received: ${it.message.event.message.contentRaw}") }
        .onEach { req -> processRequest(req) }

    private suspend fun processRequest(req: Request<EventWrapper>) {
        req.message.event.message.apply { processMessage(req) }
    }

    private suspend fun Message.processMessage(req: Request<EventWrapper>) {
        contentRaw.substringAfter("@")
            .substringBefore(" ")
            .also { name -> agentService.findAgentAndProcessInput(name, req) }
    }

    @PostConstruct
    fun setup() {
        scope.launch {
            filteredChannel.collect { request ->
                val event = request.message.event
                val message = event.message.contentRaw
                val agent = message.substringBefore(" ")
                val command = message.substringAfter(" ")
                agentService.processInput(agent, command, event.message)?.also {
                    request.message.reaction = Reaction(it)
                }
                request.response.send(Response(request.message))
            }
        }
    }
}
