package net.doemges.kogniswarm.agent

import dev.kord.core.event.message.MessageCreateEvent
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.data.Fixtures
import net.doemges.kogniswarm.discord.DiscordService
import net.doemges.kogniswarm.discord.EventWrapper
import net.doemges.kogniswarm.discord.Reaction
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import org.springframework.stereotype.Service
import java.util.*

@Service
class AgentService(
    private val discordService: DiscordService,
    private val messageChannel: Channel<Request<String>>
) {

    private val agents = mutableMapOf<String, Agent>()

    private val fixture = Fixtures.fixtureWithFaker()

    private final val scope = CoroutineScope(Dispatchers.IO)

    private val filteredChannel = discordService
            .discordEventChannel
            .receiveAsFlow()
            .filter { it.message.event is MessageCreateEvent }
            .filter { (it.message.event as MessageCreateEvent).message.content.startsWith("@") }

    init {
        repeat(5) {
            Agent(fixture(), messageChannel).let { agent ->
                agents[agent.id.name] = agent
                scope.launch { discordService.sendMessage("Agent ${agent.id.name} is ready") }
            }
        }
    }


    @PostConstruct
    fun setup() {
        scope.launch {
            filteredChannel.collect { request ->
                val event = request.message.event as MessageCreateEvent
                val message = event.message.content
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
        responseChannel.receive().message
    }

}

data class AgentIdentifier(val id: UUID, val name: String)
