package net.doemges.kogniswarm.agent

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.data.Fixtures
import net.doemges.kogniswarm.discord.DiscordService
import net.doemges.kogniswarm.discord.Reaction
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class AgentService(
    private val discordService: DiscordService, private val messageChannel: Channel<Request<String>>
) {

    private val logger: Logger = LoggerFactory.getLogger(AgentService::class.java)

    private val agents = mutableMapOf<String, Agent>()

    private val fixture = Fixtures.fixtureWithFaker()

    private final val scope = CoroutineScope(Dispatchers.IO)

    private val filteredChannel = discordService.discordEventChannel.receiveAsFlow()
            .filter { req -> agents.keys.any { req.message.event.message.contentRaw.startsWith("@$it ") } }
            .onEach { logger.info("Message received: ${it.message.event.message.contentRaw}") }
            .onEach { req ->
                val name = req.message.event.message.contentRaw.substringAfter("@")
                        .substringBefore(" ")

                agents[name]?.let { agent ->
                    val command = req.message.event.message.contentRaw.substringAfter(" ")
                    agent.executeCommand(command)
                            .also { req.message.reaction = Reaction(it) }
                }
            }

    init {
        scope.launch {
            while (!discordService.ready.get()) {
                logger.info("Waiting for Discord to be ready")
                delay(1000)
            }
            repeat(5) {
                Agent(fixture(), messageChannel).let { agent ->
                    agents[agent.id.name] = agent
                    val message = "Agent ${agent.id.name} is ready"
                    logger.info(message)
                    discordService.sendMessage(message)
                }
            }
        }

    }


    @PostConstruct
    fun setup() {
        scope.launch {
            filteredChannel.collect { request ->
                val event = request.message.event as MessageReceivedEvent
                val message = event.message.contentRaw
                val agent = message.substringBefore(" ")
                val command = message.substringAfter(" ")
                executeCommand(agent, command)?.also {
                    request.message.reaction = Reaction(it)
                }
                request.response.send(Response(request.message))
            }
        }
    }

    private suspend fun executeCommand(agent: String, command: String): String? = agents[agent]?.executeCommand(command)

}

class Agent(val id: AgentIdentifier, private val messageChannel: Channel<Request<String>>) {
    suspend fun executeCommand(command: String): String = Channel<Response<String>>().let { responseChannel ->
        messageChannel.send(Request(command, responseChannel))
        val message = responseChannel.receive().message
        "Agent ${id.name}: $message"
    }

}

data class AgentIdentifier(val id: UUID, val name: String)
