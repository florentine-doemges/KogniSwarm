package net.doemges.kogniswarm.action.route

import net.doemges.kogniswarm.action.processor.UpdateActionHistoryProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class UpdateActionHistoryRouteBuilder(private val updateActionHistoryProcessor: UpdateActionHistoryProcessor) :
    RouteBuilder() {
    override fun configure() {
        from("direct:updateActionHistory")
            .wireTap("log:updateActionHistory.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(updateActionHistoryProcessor)
            .wireTap("log:updateActionHistory.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}