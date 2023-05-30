package net.doemges.kogniswarm.tool.google

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GoogleSearchApiClient(
    @Value("\${google.search.custom.api.key}") private val googleCustomSearchApiKey: String,
    @Value("\${google.search.custom.engine.id}") private val googleCustomSearchEngineId: String,
    private val webClientBuilder: WebClient.Builder
) {

    companion object {
        private const val GOOGLE_API_URL = "https://www.googleapis.com/customsearch/v1"
    }

    fun fetchItems(query: String, start: Int, num: Int): Flow<Item> = channelFlow {
        val webClient = webClientBuilder.baseUrl(GOOGLE_API_URL)
            .build()
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
        result.forEach {
            send(it)
        }
    }
}