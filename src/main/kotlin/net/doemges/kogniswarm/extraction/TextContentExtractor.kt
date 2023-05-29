package net.doemges.kogniswarm.extraction

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.jsoup.Jsoup
import reactor.core.publisher.Mono

class TextContentExtractor : ContentExtractor {
    override fun extract(result: Mono<String>): Flow<Extract> = result
        .map { html ->
            Jsoup.parse(html)
                .body()
                .text()
        }
        .asFlow()
        .map { Extract(it) }

}