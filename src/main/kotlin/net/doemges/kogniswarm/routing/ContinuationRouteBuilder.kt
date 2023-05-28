package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.think.EndOfActionDecisionProcessor
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ContinuationRouteBuilder(private val endOfActionDecisionProcessor: EndOfActionDecisionProcessor) :
    RouteBuilder() {
    override fun configure() {
        from("direct:continue")
            .wireTap("log:continue.incoming?level=INFO&showAll=true&multiline=true")
            .process(endOfActionDecisionProcessor)
            .choice()
            .`when`(header("shouldContinue").isEqualTo(true))
            .to("direct:prompt")
            .otherwise()
            .to("direct:end")
            .wireTap("log:continue.outgoing?level=INFO&showAll=true&multiline=true")
    }
}