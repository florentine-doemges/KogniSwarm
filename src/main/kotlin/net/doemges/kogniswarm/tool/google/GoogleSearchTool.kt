package net.doemges.kogniswarm.tool.google

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.ParameterParser
import net.doemges.kogniswarm.tool.BaseTool
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class GoogleSearchTool(
    private val googleSearchApiClient: GoogleSearchApiClient,
    parameterParser: ParameterParser,
    private val objectMapper: ObjectMapper
) : BaseTool(parameterParser) {

    companion object {
        private val logger = LoggerFactory.getLogger(GoogleSearchTool::class.java)
    }

    override val name: String = "googleSearch"
    override val description: String = "searches google for the given query"
    override val args: Map<String, String> = mapOf(
        "query" to "The query to search for",
        "start" to "The starting index for search results. Default is 0",
        "num" to "The number of search results to return per page. Default is 10"
    )
    override val keys: List<String> = listOf("query", "start", "num")


    override suspend fun processWithParams(
        parsedParams: Map<String, String>,
        toolUri: String,
        toolParams: String,
        exchange: Exchange
    ) {
        logger.info("Processing $toolUri with params $toolParams -> $parsedParams")
        val query = parsedParams["query"] ?: error("Query is not specified")
        val start = parsedParams["start"]?.toIntOrNull() ?: 0
        val num = parsedParams["num"]?.toIntOrNull() ?: 10

        logProcess(toolUri, toolParams, query, start, num)

        channelFlow { tailrecFetchItems(start, num, query) }
            .toCollection(mutableListOf())
            .also { allItems -> createAction(allItems, query, start, num, exchange) }
    }

    private tailrec fun ProducerScope<Item>.tailrecFetchItems(
        start: Int,
        num: Int,
        query: String
    ) {
        if (num <= 0) return

        val currentNum = minOf(num, 10)
        val items = googleSearchApiClient.fetchItems(query, start, currentNum)

        runBlocking {
            items.onEach { send(it) }
                .collect()
        }

        tailrecFetchItems(start + currentNum, num - currentNum, query)
    }


    private fun createAction(
        allItems: List<Item>,
        query: String,
        start: Int,
        num: Int,
        exchange: Exchange
    ) {
        val result = objectMapper.writeValueAsString(allItems)
        logger.info(result)

        Action(
            this,
            mapOf("query" to query, "start" to start.toString(), "num" to num.toString()),
            result
        ).also { action ->
            exchange.message.headers["action"] = action
            logger.info(exchange.message.toString())
        }
    }

    private fun logProcess(toolUri: String, toolParams: String, query: String, start: Int, num: Int) {
        logger.info("toolUri: $toolUri")
        logger.info("toolParams: $toolParams")
        logger.info("Processing tool $toolUri with params $toolParams")
        logger.info("Query: '$query'")
        logger.info("Start: '$start'")
        logger.info("Num: '$num'")
    }
}
