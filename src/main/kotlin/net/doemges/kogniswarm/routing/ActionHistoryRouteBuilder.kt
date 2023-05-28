package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.action.ActionHistoryProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ActionHistoryRouteBuilder(private val actionHistoryProcessor: ActionHistoryProcessor) : RouteBuilder() {
    override fun configure() {
        from("direct:actionHistory")
            .wireTap("log:actionHistory.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(actionHistoryProcessor)
            .wireTap("log:actionHistory.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}