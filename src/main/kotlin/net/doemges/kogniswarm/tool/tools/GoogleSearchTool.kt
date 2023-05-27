package net.doemges.kogniswarm.tool.tools

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.tool.Tool
import org.apache.camel.Exchange
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GoogleSearchTool(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${google.search.custom.api.key}") private val googleCustomSearchApiKey: String,
    @Value("\${google.search.custom.engine.id}") private val googleCustomSearchEngineId: String,
    private val objectMapper: ObjectMapper
) : Tool {

    companion object {
        private val logger = LoggerFactory.getLogger(GoogleSearchTool::class.java)
        private const val GOOGLE_API_URL = "https://www.googleapis.com/customsearch/v1"
    }

    override val name: String = "googleSearch"
    override val description: String = "searches google for the given query"
    override val args: Map<String, String> = mapOf(
        "query" to "The query to search for",
        "start" to "The starting index for search results. Default is 0",
        "num" to "The number of search results to return per page. Default is 10"
    )

    override fun process(exchange: Exchange) {
        val toolUri = exchange.getIn().headers["toolUri"] as String
        val toolParams = exchange.getIn().headers["toolParams"] as String
        val parsedParams = parseParameters(toolParams)
        val query = parsedParams["query"] ?: error("Query is not specified")
        var start = parsedParams["start"]?.toIntOrNull() ?: 0
        val initialStart = start
        var num = parsedParams["num"]?.toIntOrNull() ?: 10
        val initialNum = num

        logProcess(toolUri, toolParams, query, start, num)

        val webClient = webClientBuilder.baseUrl(GOOGLE_API_URL)
            .build()
        val allItems = mutableListOf<Item>()
        runBlocking {
            while (num > 0) {
                val currentNum = minOf(num, 10)
                fetchItems(webClient, query, start, currentNum).collect { items ->
                    allItems.addAll(items)
                }
                num -= currentNum
                start += currentNum
            }
        }

        val result = objectMapper.writeValueAsString(allItems)
        logger.info(result)
        exchange.message.headers["action"] =
            Action(
                this,
                mapOf("query" to query, "start" to initialStart.toString(), "num" to initialNum.toString()),
                result
            )
        logger.info(exchange.message.toString())
    }

    private fun parseParameters(toolParams: String) = toolParams.split(", ")
        .map {
            it.split(":")
                .let { (key, value) ->
                    key to value.trim()
                        .removeSurrounding("\"")
                }
        }
        .toMap()

    private fun logProcess(toolUri: String, toolParams: String, query: String, start: Int, num: Int) {
        logger.info("toolUri: $toolUri")
        logger.info("toolParams: $toolParams")
        logger.info("Processing tool $toolUri with params $toolParams")
        logger.info("Query: '$query'")
        logger.info("Start: '$start'")
        logger.info("Num: '$num'")
    }

    private fun fetchItems(webClient: WebClient, query: String, start: Int, num: Int): Flow<List<Item>> = flow {
        val result = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("key", googleCustomSearchApiKey)
                    .queryParam("cx", googleCustomSearchEngineId)
                    .queryParam("q", query)
                    .queryParam("start", start + 1)
                    .queryParam("num", num)
                    .build()
            }
            .retrieve()
            .bodyToMono(Search::class.java)
            .map { it.items ?: emptyList() }
            .awaitSingle()
        emit(result)
    }

    data class Search(val items: List<Item>?)
    data class Item(val title: String, val link: String)
}