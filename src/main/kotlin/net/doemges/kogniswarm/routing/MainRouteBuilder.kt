package net.doemges.kogniswarm.routing

import net.doemges.kogniswarm.core.LogExchangeFormatter
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.log.LogComponent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MainRouteBuilder : RouteBuilder() {

    private val logger = LoggerFactory.getLogger(javaClass)
    override fun configure() {
        configureMessageHistory()
        configureErrorHandling()
        configureLogging()
        configureMainRoute()
    }

    private fun configureErrorHandling() {
        onException(Exception::class.java)
            .maximumRedeliveries(3)
            .redeliveryDelay(1000)
            .backOffMultiplier(2.0)
            .retryAttemptedLogLevel(LoggingLevel.WARN)

        logger.info("Error handling configured.")
    }

    private fun configureMessageHistory() {
        camelContext.isMessageHistory = true
        logger.info("Message history enabled.")
    }

    private fun configureLogging() {
        context
            .getComponent("log", LogComponent::class.java)
            .exchangeFormatter = LogExchangeFormatter()

        logger.info("Logging configured.")
    }

    private fun configureMainRoute() {
        from("direct:prompt")
            .wireTap("log:prompt.incoming?level=DEBUG&showAll=true&multiline=true")
            .process {
                val message = it.getIn()
                logger.info("incoming Prompt (round #${(message.headers["actionHistory"] as? List<*>)?.size ?: 0}): ${message.body}")
            }
            .to("direct:preparation")
            .to("direct:processing")
            .to("direct:postprocessing")
            .to("direct:continue")
            .wireTap("log:prompt.outgoing?level=DEBUG&showAll=true&multiline=true")
    }

}