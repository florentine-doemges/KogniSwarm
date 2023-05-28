package net.doemges.kogniswarm.context

import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.Mission
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateContextProcessor(private val memoryContext: MemoryContext) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun process(exchange: Exchange) {
        val message = exchange.getIn()
        val mission = message.body as Mission
        val h = message.headers
        val action = h["action"]
        action?.also {
            logger.info("Adding to context: $it")
            memoryContext.put(mission, it as Action)
        } ?: logger.info("No action to add to context.")
    }

}