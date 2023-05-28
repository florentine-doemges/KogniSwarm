package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.context.UpdateContextProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class UpdateContextRouteBuilder(private val updateContextProcessor: UpdateContextProcessor) : RouteBuilder() {
    override fun configure() {
        from("direct:updateContextHistory")
            .wireTap("log:updateContextHistory.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(updateContextProcessor)
            .wireTap("log:updateContextHistory.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}