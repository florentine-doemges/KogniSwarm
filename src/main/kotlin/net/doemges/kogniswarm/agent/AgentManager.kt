package net.doemges.kogniswarm.agent

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import net.doemges.kogniswarm.data.Fixtures
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import javax.annotation.PostConstruct

class AgentManager(
    private val objectMapper: ObjectMapper
) {

    private val logger: Logger = LoggerFactory.getLogger(AgentManager::class.java)

    @PostConstruct
    fun setup() {
        logger.info("AgentManager created")
    }

    private val agentFile = Paths.get("src/main/resources/agent_identifiers.json")
        .toFile()
        .also {
            if (!it.exists())
                it.createNewFile()
        }

    private val fixture = Fixtures.fixtureWithFaker()


    private val targetSize = 1
    val agents: MutableMap<String, Agent> = mutableMapOf()

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

        logger.info("AgentManager initialized")
    }

    fun extractAgentNames(messageContent: String): List<String> {
        val pattern = Regex(pattern = "@[a-zA-Z]+")
        val possibleNames = pattern.findAll(messageContent)
            .map { it.value.substring(1) }
            .toList()
        return possibleNames.filter { name -> agents.containsKey(name) }
    }


    private fun saveAgentsToFile() {
        objectMapper.writeValue(agentFile, agents.values.map { it.identifier })
        logger.info("Saved ${agents.size} agents to file $agentFile")
    }

    private fun createAgent(id: AgentIdentifier): Agent = Agent(identifier = id)
        .also { logger.info("Created agent ${id.id}") }
        .also { agents[id.name] = it }


}
