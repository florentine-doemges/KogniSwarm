package net.doemges.kogniswarm.context.processor

import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.context.service.MemoryContextService
import net.doemges.kogniswarm.mission.model.MissionKey
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class UpdateContextProcessor(private val memoryContextService: MemoryContextService) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun process(exchange: Exchange) {
        val message = exchange.getIn()
        val missionKey = message.body as MissionKey
        val h = message.headers
        val action = h["action"]
        action?.also {
            logger.debug("Adding to context: $it")
            memoryContextService.put(missionKey, it as Action)
        } ?: logger.debug("No action to add to context.")
    }

}