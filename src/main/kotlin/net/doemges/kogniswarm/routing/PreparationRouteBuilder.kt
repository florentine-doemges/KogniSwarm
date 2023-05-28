package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.core.CustomAggregationStrategy
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class PreparationRouteBuilder : RouteBuilder() {
    override fun configure() {
        from("direct:preparation")
            .wireTap("log:preparation.incoming?level=INFO&showAll=true&multiline=true")
            .multicast(CustomAggregationStrategy())
            .parallelProcessing()
            .to("direct:tools")
            .to("direct:actionHistory")
            .to("direct:context")
            .end()
            .wireTap("log:preparation.outgoing?level=INFO&showAll=true&multiline=true")
    }

}