package net.doemges.kogniswarm.tool.tools.google

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GoogleSearchApiClient(
    private val googleSearchConfig: GoogleSearchConfig,
    private val webClientBuilder: WebClient.Builder
) {

    companion object {
        private const val GOOGLE_API_URL = "https://www.googleapis.com/customsearch/v1"
    }

    fun fetchItems(query: String, start: Int, num: Int): Flow<List<Item>> = flow {
        val webClient = webClientBuilder.baseUrl(GOOGLE_API_URL)
            .build()
        val result = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("key", googleSearchConfig.googleCustomSearchApiKey)
                    .queryParam("cx", googleSearchConfig.googleCustomSearchEngineId)
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
}