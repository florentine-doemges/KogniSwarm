package net.doemges.kogniswarm.core

import org.apache.camel.Exchange
import org.apache.camel.MessageHistory
import org.apache.camel.support.processor.DefaultExchangeFormatter

class LogExchangeFormatter : DefaultExchangeFormatter() {
    @Suppress("UNCHECKED_CAST")
    override fun format(exchange: Exchange): String = buildString {
        // Append Exchange ID and Pattern
        appendLine("Exchange ID: ${exchange.exchangeId}")
        appendLine("Exchange Pattern: ${exchange.pattern}\n")

        // Append Properties if not empty
        exchange.properties.takeIf { it.isNotEmpty() }
            ?.let {
                appendLine("Properties:")
                it.forEach { (key, value) -> appendLine("$key = $value") }
            }

        // Append Message History
        (exchange.getProperty(Exchange.MESSAGE_HISTORY, List::class.java) as? List<MessageHistory>)?.let { history ->
            append("\nMessage History:\n")
            history.joinTo(this, separator = " -> ") { "${it.node.id} (${it.routeId})" }
            appendLine()
        }

        // Append Headers if not empty
        exchange.`in`.headers.takeIf { it.isNotEmpty() }
            ?.let {
                appendLine("\nHeaders:")
                it.forEach { (key, value) -> appendLine("$key = $value") }
            }

        // Append Body Type and Body
        appendLine("\nBody Type: ${exchange.`in`.body::class.qualifiedName}")
        appendLine("Body: ${exchange.`in`.body}")
    }
}
