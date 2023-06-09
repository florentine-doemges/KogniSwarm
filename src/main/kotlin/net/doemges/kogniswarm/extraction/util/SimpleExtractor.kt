package net.doemges.kogniswarm.extraction.util

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.extraction.model.Extract
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient

class SimpleExtractor(private val client: WebClient) {
    private val logger = LoggerFactory.getLogger(javaClass)


    fun extract(url: String, contentExtractor: ContentExtractor): Flow<Extract> {
        logger.info("fetching with webclient: $url")
        return contentExtractor
            .extract(
                client.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String::class.java)
            )
    }
}