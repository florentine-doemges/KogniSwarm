package net.doemges.kogniswarm.tool.tools

import com.fasterxml.jackson.databind.ObjectMapper
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
    @Value("\${google.search.custom.api.key}") val googleCustomSearchApiKey: String,
    @Value("\${google.search.custom.engine.id}") val googleCustomSearchEngineId: String,
    private val objectMapper: ObjectMapper
) : Tool {
    override val name: String = "googleSearch"
    override val description: String = "searches google for the given query"
    override val args: Map<String, String> = mapOf("query" to "The query to search for")
    override fun process(exchange: Exchange) {
        val body = exchange.getIn().body
        val toolUri = exchange.getIn().headers["toolUri"] as String
        val toolParams = exchange.getIn().headers["toolParams"] as String
        logger.info("toolUri: $toolUri")
        logger.info("toolParams: $toolParams")
        val query = toolParams.substringAfter("query:")
            .trim()
            .removeSurrounding("\"")
        logger.info("Processing tool $toolUri with params $toolParams")
        logger.info("Query: '$query'")
        logger.info("Body: $body")

        val searchUrl = "https://www.googleapis.com/customsearch/v1"
        val webClient = webClientBuilder.baseUrl(searchUrl)
            .build()
        val result = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("key", googleCustomSearchApiKey)
                    .queryParam("cx", googleCustomSearchEngineId)
                    .queryParam("q", query)
                    .build()
            }
            .retrieve()
            .bodyToMono(Search::class.java)
            .flatMapIterable { search: Search ->
                search.items ?: emptyList()
            }
            .collectList()
            .map { objectMapper.writeValueAsString(it) }
            .block() ?: error("No result")
        logger.info(result)
        exchange.message.headers["action"] = Action(this, mapOf("query" to query), result)
        logger.info(exchange.message.toString())
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    data class Search(
        val items: List<Item>?
    )

    data class Item(
        val title: String,
        val link: String
    )
}
