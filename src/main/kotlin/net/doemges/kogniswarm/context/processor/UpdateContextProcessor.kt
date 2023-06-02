package net.doemges.kogniswarm.context.processor

import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.context.service.MemoryContextService
import net.doemges.kogniswarm.core.model.Mission
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateContextProcessor(private val memoryContextService: MemoryContextService) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun process(exchange: Exchange) {
        val message = exchange.getIn()
        val mission = message.body as Mission
        val h = message.headers
        val action = h["action"]
        action?.also {
            logger.debug("Adding to context: $it")
            memoryContextService.put(mission, it as Action)
        } ?: logger.debug("No action to add to context.")
    }

}