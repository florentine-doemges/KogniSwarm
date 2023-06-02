package net.doemges.kogniswarm.tool.processor

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ToolSelectionProcessor(toolProcessors: List<ToolProcessor>) : Processor {
    private val toolProcessors: List<ToolProcessor> = toolProcessors.sortedBy { it.name }
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun process(exchange: Exchange) {
        exchange.getIn().headers["tools"] = "[" + toolProcessors.joinToString(", ") { "tool:${it.name}" } + "]"
        exchange.getIn().headers["toolDescriptions"] =
            toolProcessors.joinToString("\n") {
                "- ${it.name}: ${it.description} - args: [${
                    it.keys.joinToString(", ") { arg -> "$arg: ${it.args[arg]}" }
                }]"
            }

        logger.debug("Tools: ${exchange.getIn().headers["tools"].toString()}")
        logger.debug("ToolDescriptions: ${exchange.getIn().headers["toolDescriptions"].toString()}")
    }
}

