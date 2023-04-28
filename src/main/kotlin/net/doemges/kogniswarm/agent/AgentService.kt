package net.doemges.kogniswarm.agent

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import net.doemges.kogniswarm.data.Fixtures
import net.doemges.kogniswarm.discord.DiscordService
import net.doemges.kogniswarm.memory.Memory
import net.doemges.kogniswarm.memory.MemoryService
import net.doemges.kogniswarm.shell.ShellHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.shell.Shell
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayDeque

@Service
class AgentService(
    val discordService: DiscordService,
    private val memoryService: MemoryService,
    val chatService: ChatService,
    shell: Shell
) {
    @Value("\${discord.botToken}")
    private lateinit var botToken: String

    val shell: ShellHelper = shell as ShellHelper

    val agents = mutableListOf<Agent>()
    val fixture = Fixtures.fixtureWithFaker()

    suspend fun createAgent(scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): Agent =
        Agent(
            agentService = this,
            scope = scope,
            memory = memoryService.createMemory()
        )
                .apply { start() }
                .also { agents.add(it) }

    fun deleteAgent(agent: Agent) {
        if (agent.running) agent.stop()
        agents.remove(agent)
    }

    fun listAgents(): List<Agent> = agents
}

data class AgentIdentifier(val id: UUID, val name: String)

class Agent(
    private val agentService: AgentService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val memory: Memory<Message>
) {
    private val discordService: DiscordService = agentService.discordService
    private val chatService: ChatService = agentService.chatService
    private val shell: ShellHelper = agentService.shell
    val id: AgentIdentifier = agentService.fixture()
    var running: Boolean = false

    private val queue = ArrayDeque<Message>()

    suspend fun start() {
        scope.launch {
            discordService.listen()
                    .filter { it.content.startsWith("@${id.name}") }
                    .collect { onMessage(it) }
        }
        scope.launch { processQueue() }
        running = true
    }

    private suspend fun processQueue() {
        while (running) {
            if (queue.isNotEmpty()) {
                val message = queue.removeFirst()
                processMessage(message)
            } else {
                delay(1000) // Delay 1 second before checking again
            }
        }
    }

    private fun processMessage(message: Message) {
        memory.commit(message)
        val result = shell.evaluate(message.content)
        discordService.sendMessageToChannel(message.channelId.toString(), "@${id.name}: $result")
    }
q
    private fun onMessage(message: Message): Message = message
            .also { queue.add(message) }


    fun stop() {
        running = false
    }

}
