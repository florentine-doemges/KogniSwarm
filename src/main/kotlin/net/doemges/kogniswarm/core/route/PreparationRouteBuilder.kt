package net.doemges.kogniswarm.core.route

import net.doemges.kogniswarm.core.util.CustomAggregationStrategy
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class PreparationRouteBuilder : RouteBuilder() {
    override fun configure() {
        from("direct:preparation")
            .wireTap("log:preparation.incoming?level=DEBUG&showAll=true&multiline=true")
            .multicast(CustomAggregationStrategy())
            .parallelProcessing()
            .to("direct:tools")
            .to("direct:actionHistory")
            .to("direct:context")
            .end()
            .wireTap("log:preparation.outgoing?level=DEBUG&showAll=true&multiline=true")
    }

}