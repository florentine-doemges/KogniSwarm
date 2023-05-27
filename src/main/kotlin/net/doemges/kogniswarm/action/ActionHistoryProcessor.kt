package net.doemges.kogniswarm.action

import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import net.doemges.kogniswarm.core.Mission
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ActionHistoryProcessor(private val actionHistory: ActionHistory) : Processor {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun process(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        val history = actionHistory.get(mission)
        logger.info("History Size: ${history?.size}")
        exchange.getIn().headers["actionHistory"] = history
    }

}