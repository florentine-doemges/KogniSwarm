package net.doemges.kogniswarm.tool.route

import net.doemges.kogniswarm.tool.processor.ToolSelectionProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ToolsRouteBuilder(private val toolSelectionProcessor: ToolSelectionProcessor) : RouteBuilder() {
    override fun configure() {
        from("direct:tools")
            .wireTap("log:tools.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(toolSelectionProcessor)
            .wireTap("log:tools.outgoing?level=DEBUG&showAll=true&multiline=true")
    }
}