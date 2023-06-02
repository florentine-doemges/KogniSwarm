package net.doemges.kogniswarm.context.route

import net.doemges.kogniswarm.context.processor.ContextProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ContextRouteBuilder(private val contextProcessor: ContextProcessor) : RouteBuilder() {
    override fun configure() {
        from("direct:context")
            .wireTap("log:context.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(contextProcessor)
            .wireTap("log:context.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}