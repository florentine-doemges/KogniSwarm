package net.doemges.kogniswarm.action

import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import net.doemges.kogniswarm.core.Mission
import org.apache.camel.Exchange
import org.springframework.stereotype.Component

@Component
class UpdateActionHistoryProcessor(private val actionHistory: ActionHistory) : CoroutineAsyncProcessor() {
    override suspend fun processSuspend(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        exchange.getIn().headers["action"]?.also {
            actionHistory.put(mission, it as Action)
        }
    }

}