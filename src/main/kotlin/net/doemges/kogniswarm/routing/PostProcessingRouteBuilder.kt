package net.doemges.kogniswarm.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class PostProcessingRouteBuilder : RouteBuilder() {
    override fun configure() {
        from("direct:postprocessing")
            .wireTap("log:postprocessing.incoming?level=INFO&showAll=true&multiline=true")
            .to("direct:summarizeAction")
            .to("direct:updateActionHistory")
            .to("direct:updateContextHistory")
            .wireTap("log:postprocessing.outgoing?level=INFO&showAll=true&multiline=true")
    }
}