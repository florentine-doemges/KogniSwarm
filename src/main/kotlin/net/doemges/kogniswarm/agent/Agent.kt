package net.doemges.kogniswarm.agent

import net.doemges.kogniswarm.core.Message
import org.slf4j.LoggerFactory


class Agent(
    val identifier: AgentIdentifier,
    private val agentCommandParser: AgentCommandParser = AgentCommandParser()
) {
    val missions: MutableList<Mission> = mutableListOf()

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun processMessage(message: Message<*>, postProcess: suspend (Message<*>) -> Unit = {}) {
        val msg = message.payload as net.dv8tion.jda.api.entities.Message
        val content = msg.contentRaw
        logger.info("Received message $content")
        if (content.startsWith("${identifier.name}: ")) {
            logger.info("Message is from me")
            return
        }

        val response = "${identifier.name}: " + try {
            agentCommandParser.processCommand(this, content)
        } catch (e: Exception) {
            e.message ?: "Unknown error"
        }
        logger.info("Response $response")
        val agentResponse = AgentResponse(response, message.payload)
        logger.info("Agent response $agentResponse")
        val out = Message(agentResponse)
        logger.info("Out message $out")
        postProcess(out)

    }
}

data class AgentResponse(val response: String, val message: net.dv8tion.jda.api.entities.Message)

data class Mission(val name: String, val description: String, var status: MissionStatus = MissionStatus.INACTIVE)

enum class MissionStatus {
    ACTIVE, INACTIVE;
}
