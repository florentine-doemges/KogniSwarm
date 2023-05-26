package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.action.ActionHistoryProcessor
import net.doemges.kogniswarm.action.ActionSummaryProcessor
import net.doemges.kogniswarm.action.UpdateActionHistoryProcessor
import net.doemges.kogniswarm.context.ContextProcessor
import net.doemges.kogniswarm.context.UpdateContextProcessor
import net.doemges.kogniswarm.core.CustomAggregationStrategy
import net.doemges.kogniswarm.core.LogExchangeFormatter
import net.doemges.kogniswarm.openai.OpenAIChatCompletionProcessor
import net.doemges.kogniswarm.think.ChainOfThoughtProcessor
import net.doemges.kogniswarm.think.EndOfActionDecisionProcessor
import net.doemges.kogniswarm.tool.ToolSelectionProcessor
import org.apache.camel.CamelContext
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.log.LogComponent
import org.springframework.stereotype.Component


@Component
class KogniSwarmRouteBuilder(
    private val chainOfThoughtProcessor: ChainOfThoughtProcessor,
    private val endOfActionDecisionProcessor: EndOfActionDecisionProcessor,
    private val toolSelectionProcessor: ToolSelectionProcessor,
    private val actionHistoryProcessor: ActionHistoryProcessor,
    private val contextProcessor: ContextProcessor,
    private val updateActionHistoryProcessor: UpdateActionHistoryProcessor,
    private val updateContextHistoryProcessor: UpdateContextProcessor,
    private val openAIChatCompletionProcessor: OpenAIChatCompletionProcessor,
    private val actionSummaryProcessor: ActionSummaryProcessor,
    private val camelContext: CamelContext
) : RouteBuilder() {
    override fun configure() {

        camelContext.isMessageHistory = true

        onException(Exception::class.java)
            .maximumRedeliveries(3)
            .redeliveryDelay(1000)
            .backOffMultiplier(2.0)
            .retryAttemptedLogLevel(LoggingLevel.WARN)


        from("direct:prompt")
            .wireTap("log:prompt.incoming?level=DEBUG&showAll=true&multiline=true")
            .to("direct:preparation")
            .to("direct:processing")
            .to("direct:postprocessing")
            .wireTap("log:prompt.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:preparation")
            .wireTap("log:preparation.incoming?level=DEBUG&showAll=true&multiline=true")
            .multicast(CustomAggregationStrategy())
            .parallelProcessing()
            .to("direct:tools")
            .to("direct:actionHistory")
            .to("direct:context")
            .end()
            .wireTap("log:preparation.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:processing")
            .wireTap("log:processing.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(chainOfThoughtProcessor)
            .wireTap("log:chainOfThoughtProcessor.outgoing?level=DEBUG&showAll=true&multiline=true")
            .recipientList(header("toolUri"))
            .wireTap("log:tool.outgoing?level=DEBUG&showAll=true&multiline=true")
            .process(actionSummaryProcessor)
            .wireTap("log:processing.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:postprocessing")
            .wireTap("log:postprocessing.incoming?level=DEBUG&showAll=true&multiline=true")
            .to("direct:updateActionHistory")
            .to("direct:updateContextHistory")
            .process(endOfActionDecisionProcessor)
            .wireTap("log:endOfActionDecisionProcessor.outgoing?level=DEBUG&showAll=true&multiline=true")
            .choice()
            .`when`(header("shouldContinue").isEqualTo(true))
            .to("direct:prompt")
            .otherwise()
            .to("direct:end")
            .wireTap("log:postprocessing.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:tools")
            .wireTap("log:tools.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(toolSelectionProcessor)
            .wireTap("log:tools.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:actionHistory")
            .wireTap("log:actionHistory.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(actionHistoryProcessor)
            .wireTap("log:actionHistory.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:context")
            .wireTap("log:context.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(contextProcessor)
            .wireTap("log:context.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:updateContextHistory")
            .wireTap("log:updateContextHistory.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(updateContextHistoryProcessor)
            .wireTap("log:updateContextHistory.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:updateActionHistory")
            .wireTap("log:updateActionHistory.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(updateActionHistoryProcessor)
            .wireTap("log:updateActionHistory.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:openai-chatcompletion")
            .wireTap("log:openai-chatcompletion.incoming?level=DEBUG&showAll=true&multiline=true")
            .process(openAIChatCompletionProcessor)
            .wireTap("log:openai-chatcompletion.outgoing?level=DEBUG&showAll=true&multiline=true")

        from("direct:end")
            .wireTap("log:end.incoming?level=DEBUG&showAll=true&multiline=true")

        context
            .getComponent("log", LogComponent::class.java)
            .exchangeFormatter = LogExchangeFormatter()
    }

}


