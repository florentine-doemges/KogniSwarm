package net.doemges.kogniswarm.core

import org.apache.camel.Exchange
import org.apache.camel.MessageHistory
import org.apache.camel.support.processor.DefaultExchangeFormatter

class LogExchangeFormatter : DefaultExchangeFormatter() {
    @Suppress("UNCHECKED_CAST")
    override fun format(exchange: Exchange): String {
        val builder = StringBuilder()

        // Append Exchange ID and Pattern
        builder.append("\nExchange ID: ")
            .append(exchange.exchangeId)
            .append("\n")
        builder.append("Exchange Pattern: ")
            .append(exchange.pattern)
            .append("\n\n")

        if (exchange.properties.isNotEmpty()){
            builder.append("Properties:\n")
            exchange.properties.forEach { (key, value) -> builder.append("$key = $value\n") }
        }

        // Append Message History
        val history: List<MessageHistory>? =
            exchange.getProperty(Exchange.MESSAGE_HISTORY, List::class.java) as List<MessageHistory>?
        if (history != null) {
            builder.append("\nMessage History:\n")
            for (i in history.indices) {
                val node = history[i]
                builder.append("${node.node.id} (${node.routeId}) -> ")
            }
        }

        // Append Headers
        if (exchange.`in`.headers.isNotEmpty()){
            builder.append("\nHeaders:\n")
            exchange.`in`.headers.forEach { (key, value) -> builder.append("$key = $value\n") }
        }

        // Append Body Type and Body
        builder.append("\nBody Type: ")
            .append(exchange.`in`.body::class.qualifiedName)
            .append("\n")
        builder.append("Body: ")
            .append(exchange.`in`.body)
        builder.append("\n")

        return builder.toString()
    }
}