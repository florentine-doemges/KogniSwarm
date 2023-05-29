package net.doemges.kogniswarm.extraction

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.reactive.asFlow
import org.jsoup.Jsoup
import reactor.core.publisher.Mono

@OptIn(FlowPreview::class)
class ImageContentExtractor : ContentExtractor {
    override fun extract(result: Mono<String>): Flow<Extract> = result
        .map { html ->
            Jsoup.parse(html)
                .select("img[src]")
        }
        .asFlow()
        .flatMapConcat { elements ->
            elements.map { Extract(it.attr("abs:src")) }
                .asFlow()
        }

}