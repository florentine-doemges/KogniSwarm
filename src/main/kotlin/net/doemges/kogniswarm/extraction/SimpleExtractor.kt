package net.doemges.kogniswarm.extraction

import kotlinx.coroutines.flow.Flow
import org.springframework.web.reactive.function.client.WebClient

class SimpleExtractor(private val client: WebClient) {
    fun extract(url: String, contentExtractor: ContentExtractor): Flow<Extract> = contentExtractor
        .extract(
            client.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String::class.java)
        )
}