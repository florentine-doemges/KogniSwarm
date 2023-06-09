package net.doemges.kogniswarm.context.route

import net.doemges.kogniswarm.context.processor.UpdateContextProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class UpdateContextRouteBuilder(private val updateContextProcessor: UpdateContextProcessor) : RouteBuilder() {
    override fun configure() {
        from("direct:updateContextHistory")
            .wireTap("log:updateContextHistory.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(updateContextProcessor)
            .wireTap("log:updateContextHistory.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}