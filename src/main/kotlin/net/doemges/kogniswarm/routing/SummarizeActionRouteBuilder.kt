package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.action.ActionSummaryProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class SummarizeActionRouteBuilder(private val actionSummaryProcessor: ActionSummaryProcessor) : RouteBuilder() {
    override fun configure() {
        from("direct:summarizeAction")
            .wireTap("log:summarizeAction.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(actionSummaryProcessor)
            .wireTap("log:summarizeAction.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}