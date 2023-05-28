package net.doemges.kogniswarm.openai

import com.aallam.openai.api.model.Model
import com.aallam.openai.client.OpenAI
import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import org.apache.camel.Exchange
import org.springframework.stereotype.Component

@Component
class OpenAIModelProcessor(
    private val openAI: OpenAI
) : CoroutineAsyncProcessor() {
    override suspend fun processSuspend(exchange: Exchange) {
        val openAIModelRequest: OpenAIModelRequest? = exchange.getIn().body as? OpenAIModelRequest
        exchange.message.body = openAI
            .models()
            .filter { model: Model ->
                openAIModelRequest == null || openAIModelRequest.matches(model)
            }
            .sortedByDescending { it.created }
    }
}


