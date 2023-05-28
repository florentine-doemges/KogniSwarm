package net.doemges.kogniswarm.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class EndRouteBuilder : RouteBuilder() {
    override fun configure() {
        from("direct:end")
            .wireTap("log:end.incoming?level=INFO&showAll=true&multiline=true")
    }
}