package net.doemges.kogniswarm.action

import net.doemges.kogniswarm.core.Mission
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateActionHistoryProcessor(private val actionHistory: ActionHistory) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun process(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        exchange.getIn().headers["action"]?.also {
            logger.info("Adding to action history: $it")
            actionHistory.put(mission, it as Action)
        } ?: logger.info("No action to add to action history.")
    }

}