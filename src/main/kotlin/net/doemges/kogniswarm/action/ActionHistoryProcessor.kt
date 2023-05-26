package net.doemges.kogniswarm.action

import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import net.doemges.kogniswarm.core.Mission
import org.apache.camel.Exchange
import org.springframework.stereotype.Component

@Component
class ActionHistoryProcessor(private val actionHistory: ActionHistory) : CoroutineAsyncProcessor() {
    override suspend fun processSuspend(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        val history = actionHistory.get(mission)
        exchange.getIn().headers["actionHistory"] = history
    }

}