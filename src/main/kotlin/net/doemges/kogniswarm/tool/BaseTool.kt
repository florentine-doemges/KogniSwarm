package net.doemges.kogniswarm.tool

import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.core.ParameterParser
import org.apache.camel.Exchange

abstract class BaseTool(private val parameterParser: ParameterParser) : Tool {
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