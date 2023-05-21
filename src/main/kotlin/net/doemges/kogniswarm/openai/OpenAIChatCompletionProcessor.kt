package net.doemges.kogniswarm.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import org.apache.camel.Exchange
import org.springframework.stereotype.Component

@Component
class OpenAIChatCompletionProcessor(
    private val openAI: OpenAI,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : CoroutineAsyncProcessor(scope) {
    @OptIn(BetaOpenAI::class)
    override suspend fun processSuspend(exchange: Exchange) {
        exchange.message.body =
            openAI.chatCompletion((exchange.getIn().body as OpenAIChatCompletionRequest).asChatCompletionRequest(openAI))
    }
}

