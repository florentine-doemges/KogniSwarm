package net.doemges.kogniswarm.core.route

import net.doemges.kogniswarm.think.processor.ChainOfThoughtProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ProcessingRouteBuilder(private val chainOfThoughtProcessor: ChainOfThoughtProcessor) : RouteBuilder() {
    override fun configure() {
        errorHandler(defaultErrorHandler()
            .maximumRedeliveries(5)
            .redeliveryDelay(1000))

        from("direct:processing")
            .errorHandler(noErrorHandler())
            .wireTap("log:processing.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(chainOfThoughtProcessor)
            .wireTap("log:recipientList.incoming?level=DEBUG&showAll=true&multiline=true")
            .recipientList(header("toolUri"))
            .wireTap("log:processing.outgoing?level=DEBUG&showAll=true&multiline=true")
    }

}