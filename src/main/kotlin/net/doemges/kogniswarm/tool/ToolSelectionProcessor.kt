package net.doemges.kogniswarm.tool

import jakarta.annotation.PostConstruct
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ToolSelectionProcessor(tools: List<Tool>) : Processor {
    private val tools: List<Tool> = tools.sortedBy { it.name }
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun process(exchange: Exchange) {
        exchange.getIn().headers["tools"] = "[" + tools.joinToString(", ") { "tool:${it.name}" } + "]"
        exchange.getIn().headers["toolDescriptions"] =
            tools.joinToString("\n") {
                "- ${it.name}: ${it.description} - args: [${
                    it.keys.joinToString(", ") { arg -> "$arg: ${it.args[arg]}" }
                }]"
            }

        logger.info("Tools: ${exchange.getIn().headers["tools"].toString()}")
        logger.info("ToolsDescription: ${exchange.getIn().headers["toolsDescription"].toString()}")
    }
}

