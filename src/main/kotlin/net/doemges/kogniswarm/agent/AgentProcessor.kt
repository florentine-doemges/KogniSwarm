package net.doemges.kogniswarm.agent

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.config.SerializationConfig
import net.doemges.kogniswarm.core.processor.BaseProcessor
import net.doemges.kogniswarm.core.processor.BaseProcessorBuilder
import net.doemges.kogniswarm.core.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.LoggerFactory

class AgentProcessor(
    input: Flow<Message<*>>? = null
) : BaseProcessor(
    input
) {

    private val objectMapper = SerializationConfig.objectMapper()

    private val agentManager: AgentManager = AgentManager(objectMapper)

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        val agents = agentManager.agents
        logger.info("Found agents $agents: ${agents.keys} }}")
    }

    companion object {
        fun builder(block: Builder.() -> Unit = {}): Builder =
            Builder().apply(block)
    }

    override suspend fun processMessage(message: Message<*>) {
        super.processMessage(message)
        val payload = message.payload as net.dv8tion.jda.api.entities.Message
        val content = payload.contentRaw
        logger.info("Received message $content")
        val agentNames = agentManager.extractAgentNames(content)
        logger.info("Found agent names $agentNames")
        agentNames.forEach { agentName ->
            agentManager.agents[agentName]?.let { agent ->
                logger.info("Found agent $agentName")
                agent.processMessage(message) {
                    logger.info("Sending message $it")
                    channel.send(it)
                }
            }
        }
    }

    class Builder : BaseProcessorBuilder<AgentProcessor>() {
        override fun build(): AgentProcessor = AgentProcessor(input)

    }
}
