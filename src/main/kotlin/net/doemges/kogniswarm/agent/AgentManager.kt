package net.doemges.kogniswarm.agent

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import kotlinx.coroutines.channels.SendChannel
import net.doemges.kogniswarm.assistant.AssistantRequest
import net.doemges.kogniswarm.assistant.AssistantResponse
import net.doemges.kogniswarm.data.Fixtures
import net.doemges.kogniswarm.discord.DiscordRequest
import net.doemges.kogniswarm.discord.DiscordResponse
import net.doemges.kogniswarm.io.RequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths

class AgentManager(
    private val assistant: SendChannel<RequestMessage<AssistantRequest, AssistantResponse>>,
    val output: SendChannel<RequestMessage<DiscordRequest, DiscordResponse>>,
    private val objectMapper: ObjectMapper
) {

    private val logger: Logger = LoggerFactory.getLogger(AgentManager::class.java)

    private val agentFile = Paths.get("src/main/resources/agent_identifiers.json")
        .toFile()
        .also {
            if (!it.exists())
                it.createNewFile()
        }

    private val fixture = Fixtures.fixtureWithFaker()


    private val targetSize = 1
    private val agents: MutableMap<String, Agent> = mutableMapOf()

    init {
        try {
            objectMapper.readValue(agentFile, object : TypeReference<List<AgentIdentifier>>() {})

        } catch (ignored: MismatchedInputException) {
            emptyList()
        }
            .take(targetSize)
            .forEach { createAgent(it) }

        logger.info("Loaded ${agents.size} agents from file $agentFile")

        if (agents.size != targetSize) {
            logger.info("Creating ${targetSize - agents.size} agents")
        }

        while (agents.size < targetSize) {
            val id: AgentIdentifier = fixture()
            val agent = createAgent(id)
            agents[id.name] = agent
        }
        Runtime.getRuntime()
            .addShutdownHook(Thread {
                saveAgentsToFile()
            })
    }

    private fun saveAgentsToFile() {
        objectMapper.writeValue(agentFile, agents.values.map { it.identifier })
        logger.info("Saved ${agents.size} agents to file $agentFile")
    }

    private fun createAgent(id: AgentIdentifier): Agent = Agent(id, assistant, output)
        .also { logger.info("Created agent ${id.id}") }
        .also { agents[id.name] = it }


    fun getByNameOrNull(name: String): Agent? = agents[name]

}