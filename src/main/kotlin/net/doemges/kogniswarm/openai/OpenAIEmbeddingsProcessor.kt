package net.doemges.kogniswarm.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.springframework.stereotype.Component

@Component
class OpenAIEmbeddingsProcessor(
    private val openAI: OpenAI,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : CoroutineAsyncProcessor(scope) {
    override suspend fun processSuspend(exchange: Exchange) {
        exchange.message.body = openAI.embeddings(exchange.getIn().body as EmbeddingRequest)
    }
}