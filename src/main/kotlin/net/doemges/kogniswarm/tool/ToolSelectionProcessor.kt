package net.doemges.kogniswarm.tool

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.springframework.stereotype.Component

@Component
class ToolSelectionProcessor(val tools: List<Tool>) : Processor {
    override fun process(exchange: Exchange) {
        exchange.getIn().headers["tools"] = "[" + tools.joinToString(", ") { "tool:${it.name}" } + "]"
        exchange.getIn().headers["toolDescriptions"] =
            tools.joinToString("\n") {
                "- ${it.name}: ${it.description} - args: [${
                    it.args.keys.joinToString(", ") { arg -> "$arg: ${it.args[arg]}" }
                }]"
            }
    }
}

