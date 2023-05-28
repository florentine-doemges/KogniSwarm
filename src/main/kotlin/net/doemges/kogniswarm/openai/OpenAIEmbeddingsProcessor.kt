package net.doemges.kogniswarm.openai

import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.client.OpenAI
import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import org.apache.camel.Exchange
import org.springframework.stereotype.Component

@Component
class OpenAIEmbeddingsProcessor(
    private val openAI: OpenAI
) : CoroutineAsyncProcessor() {
    override suspend fun processSuspend(exchange: Exchange) {
        exchange.message.body = openAI.embeddings(exchange.getIn().body as EmbeddingRequest)
    }
}