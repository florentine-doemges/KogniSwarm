package net.doemges.kogniswarm

import net.doemges.kogniswarm.agent.AgentProcessor
import net.doemges.kogniswarm.assistant.AssistantConsumer
import net.doemges.kogniswarm.core.KogniSwarm
import net.doemges.kogniswarm.discord.DiscordConsumer
import net.doemges.kogniswarm.discord.DiscordProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class KogniSwarmRunner(@Value("\${discord.token}") private val token: String) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun run(args: ApplicationArguments?) {
        logger.info("Starting KogniSwarm")
        KogniSwarm
            .builder {
                producer(DiscordProducer.builder()) {
                    token(token)
                }
                processor(AgentProcessor.builder()) {
                    consumer(AssistantConsumer.builder())
                }
                consumer(DiscordConsumer.builder())

            }
            .build()
            .start()
    }


}


