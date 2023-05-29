package net.doemges.kogniswarm.extraction

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono

interface ContentExtractor {
    fun extract(result: Mono<String>): Flow<Extract>
}