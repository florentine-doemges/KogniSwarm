package net.doemges.kogniswarm.openai

import com.aallam.openai.api.model.Model
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.doemges.kogniswarm.core.CoroutineAsyncProcessor
import org.apache.camel.Exchange
import org.springframework.stereotype.Component

@Component
class OpenAIModelProcessor(
    private val openAI: OpenAI,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : CoroutineAsyncProcessor(scope) {
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

fun List<Model>.joinToString() = joinToString("\n") { model ->
    "\n${model.id.id}\nownedBy: ${model.ownedBy}\tcreated: ${model.created}\tpermission:\n${
        model.permission.joinToString("\n\t") { modelPermission ->
            with(modelPermission) {
                """   id:                 $id
                        |   organization:       $organization
                        |   created:            $created
                        |   isBlocking:         $isBlocking
                        |   allowCreateEngine:  $allowCreateEngine
                        |   allowSampling:      $allowSampling
                        |   allowLogprobs:      $allowLogprobs
                        |   allowView:          $allowView
                        |   allowFineTuning:    $allowFineTuning
                        |   allowSearchIndices: $allowSearchIndices
                    """.trimMargin()
            }

        }
    }"
}


