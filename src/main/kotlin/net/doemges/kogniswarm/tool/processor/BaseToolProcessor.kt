package net.doemges.kogniswarm.tool.processor

import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.core.util.ParameterParser
import org.apache.camel.Exchange

abstract class BaseToolProcessor(private val parameterParser: ParameterParser) : ToolProcessor {
    override fun process(exchange: Exchange): Unit = runBlocking {
        (exchange.getIn().headers["toolUri"] as? String)?.also { toolUri ->
            (exchange.getIn().headers["toolParams"] as? String)?.let { toolParams ->
                parameterParser.parseParameters(toolParams, keys)
                    .also { parsedParams ->
                        processWithParams(parsedParams, toolUri, toolParams, exchange)
                    }
            }
        }
    }

    abstract suspend fun processWithParams(
        parsedParams: Map<String, String>,
        toolUri: String,
        toolParams: String,
        exchange: Exchange
    )
}