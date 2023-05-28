package net.doemges.kogniswarm.tool.tools.google

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.tool.Tool
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GoogleSearchTool(
    private val googleSearchApiClient: GoogleSearchApiClient,
    private val parameterParser: ParameterParser,
    private val objectMapper: ObjectMapper
) : Tool {

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

    @OptIn(FlowPreview::class)
    override fun process(exchange: Exchange): Unit = runBlocking {
        (exchange.getIn().headers["toolUri"] as? String)?.also { toolUri ->
            (exchange.getIn().headers["toolParams"] as? String)?.let { toolParams ->
                parameterParser.parseParameters(toolParams)
                    .also { parsedParams ->
                        val (query, start, num) = checkParams(parsedParams)

                        logProcess(toolUri, toolParams, query, start, num)

                        flow { tailrecFetchItems(start, num, query) }
                            .flattenConcat()
                            .toList()
                            .also { allItems -> createAction(allItems, query, start, num, exchange) }
                    }
            }
        }
    }

    private fun checkParams(parsedParams: Map<String, String>): Triple<String, Int, Int> {
        val query = parsedParams["query"] ?: error("Query is not specified")
        val start = parsedParams["start"]?.toIntOrNull() ?: 0
        val num = parsedParams["num"]?.toIntOrNull() ?: 10
        return Triple(query, start, num)
    }

    private tailrec fun FlowCollector<Flow<List<Item>>>.tailrecFetchItems(
        start: Int,
        num: Int,
        query: String
    ) {
        if (num <= 0) return

        val currentNum = minOf(num, 10)
        runBlocking { emit(googleSearchApiClient.fetchItems(query, start, currentNum)) }

        tailrecFetchItems(start + currentNum, num - currentNum, query)
    }


    private fun createAction(
        allItems: List<List<Item>>,
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
