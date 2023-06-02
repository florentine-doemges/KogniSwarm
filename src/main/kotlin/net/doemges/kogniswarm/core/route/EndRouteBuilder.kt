package net.doemges.kogniswarm.core.route

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class EndRouteBuilder : RouteBuilder() {
    override fun configure() {
        from("direct:end")
            .wireTap("log:end.incoming?level=DEBUG&showAll=true&multiline=true")
    }
}