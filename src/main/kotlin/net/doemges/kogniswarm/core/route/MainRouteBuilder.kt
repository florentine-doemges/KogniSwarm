package net.doemges.kogniswarm.core.route

import net.doemges.kogniswarm.core.util.LogExchangeFormatter
import net.doemges.kogniswarm.mission.model.MissionKey
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.log.LogComponent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MainRouteBuilder(@Value("\${camel.message.history:false}") private val messageHistory: Boolean = false) :
    RouteBuilder() {

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

        logger.debug("Error handling configured.")
    }

    private fun configureMessageHistory() {
        camelContext.isMessageHistory = messageHistory
        logger.debug("Message history ${if (messageHistory) "enabled" else "disabled"}.")
    }

    private fun configureLogging() {
        context
            .getComponent("log", LogComponent::class.java)
            .exchangeFormatter = LogExchangeFormatter()

        logger.debug("Logging configured.")
    }

    private fun configureMainRoute() {
        from("direct:prompt")
            .wireTap("log:prompt.incoming?level=DEBUG&showAll=true&multiline=true")
            .process {
                val message = it.getIn()
                logger.debug("incoming Prompt (round #${(message.headers["actionHistory"] as? List<*>)?.size ?: 0}): ${message.body}")
                logger.info("Prompt: ${(message.body as MissionKey).userPrompt}")
            }
            .to("direct:preparation")
            .to("direct:processing")
            .to("direct:postprocessing")
            .to("direct:continue")
            .wireTap("log:prompt.outgoing?level=DEBUG&showAll=true&multiline=true")
    }

}