package net.doemges.kogniswarm.agent

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import net.doemges.kogniswarm.data.Fixtures
import net.doemges.kogniswarm.discord.DiscordService
import net.doemges.kogniswarm.discord.EventWrapper
import net.doemges.kogniswarm.discord.Reaction
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import net.doemges.kogniswarm.memory.MemoryService
import net.doemges.kogniswarm.shell.ShellTask
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
@DependsOn("discordService")
class AgentService(
    private val discordService: DiscordService,
    private val memoryService: MemoryService,
    private val chatGptChannel: Channel<Request<String>>,
    private val objectMapper: ObjectMapper
) {

    private val logger: Logger = LoggerFactory.getLogger(AgentService::class.java)

    private val agents = mutableMapOf<String, Agent>()

    private val fixture = Fixtures.fixtureWithFaker()

    private final val scope = CoroutineScope(Dispatchers.IO)

    private val filteredChannel = discordService.discordEventChannel.receiveAsFlow()
            .filter { req -> agents.keys.any { req.message.event.message.contentRaw.startsWith("@$it ") } }
            .onEach { logger.info("Message received: ${it.message.event.message.contentRaw}") }
            .onEach { req -> processRequest(req) }

    private suspend fun processRequest(req: Request<EventWrapper>) {
        req.message.event.message.apply { processMessage(req) }
    }

    private suspend fun Message.processMessage(req: Request<EventWrapper>) {
        contentRaw.substringAfter("@")
                .substringBefore(" ")
                .also { name -> findAgentAndProcessInput(name, req) }
    }

    private suspend fun Message.findAgentAndProcessInput(
        name: String,
        req: Request<EventWrapper>
    ) {
        agents[name]?.processInput(contentRaw.substringAfter(" "), this)
                ?.also { output -> setReaction(req, output) }
    }

    private fun setReaction(
        req: Request<EventWrapper>,
        output: String
    ) {
        req.message.reaction = Reaction(output)
    }

    private val agentFile = Paths.get("src/main/resources/agent_identifiers.json")
            .toFile()

    private val targetSize = 1

    init {
        scope.launch {
            waitForDiscordToBeReady()

            readAgentsFromFile()


            createMissingAgents()
        }

    }

    private suspend fun AgentService.createMissingAgents() {
        if (agents.size != targetSize) {
            logger.info("Creating ${targetSize - agents.size} agents")
        }

        while (agents.size < targetSize) {
            createAgent(fixture())
        }
    }

    private suspend fun AgentService.readAgentsFromFile() {
        objectMapper.readValue(agentFile, object : TypeReference<List<AgentIdentifier>>() {})
                .take(targetSize)
                .forEach { createAgent(it) }

        logger.info("Loaded ${agents.size} agents from file $agentFile")
    }

    private suspend fun waitForDiscordToBeReady() {
        while (!discordService.ready.get()) {
            logger.info("Waiting for Discord to be ready")
            delay(1000)
        }
    }


    private suspend fun createAgent(it: AgentIdentifier) {
        Agent(it, chatGptChannel, memoryService.createMemory(it.id.toString())).let { agent ->
            agents[agent.id.name] = agent
            "Agent ${agent.id.name} is ready".let { message ->
                logger.info(message)
                discordService.sendMessage(message)
            }
        }
    }


    @PostConstruct
    fun setup() {
        scope.launch {
            filteredChannel.collect { request ->
                val event = request.message.event
                val message = event.message.contentRaw
                val agent = message.substringBefore(" ")
                val command = message.substringAfter(" ")
                processInput(agent, command, event.message)?.also {
                    request.message.reaction = Reaction(it)
                }
                request.response.send(Response(request.message))
            }
        }
    }

    private suspend fun processInput(agent: String, command: String, message: Message): String? = agents[agent]
            ?.processInput(command, message)


}

