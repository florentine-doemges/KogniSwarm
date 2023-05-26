package net.doemges.kogniswarm.context

import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.Mission
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.springframework.stereotype.Component

@Component
class UpdateContextProcessor(private val memoryContext: MemoryContext) : Processor {
    override fun process(exchange: Exchange) {
        val mission = exchange.getIn().body as Mission
        exchange.getIn().headers["action"]?.also {
            memoryContext.put(mission, it as Action)
        }
    }

}