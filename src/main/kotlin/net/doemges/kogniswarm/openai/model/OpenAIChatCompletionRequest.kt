package net.doemges.kogniswarm.openai.model

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import org.slf4j.LoggerFactory

class OpenAIChatCompletionRequest(
    val model: ModelId? = null,
    val modelRequest: OpenAIModelRequest? = null,
    val messages: List<OpenAIChatMessage>? = null,
    val temperature: Double? = null,
    val topP: Double? = null,
    val n: Int? = null,
    val stop: List<String>? = null,
    val maxTokens: Int? = null,
    val presencePenalty: Double? = null,
    val frequencyPenalty: Double? = null,
    val logitBias: Map<String, Int>? = null,
    val user: String? = null,
    val optimize: Boolean = false
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @OptIn(BetaOpenAI::class)
    suspend fun asChatCompletionRequest(openAI: OpenAI): ChatCompletionRequest =
        ChatCompletionRequest(model = model ?: openAI.models()
            .firstOrNull { modelRequest?.matches(it) ?: false }?.id ?: error("model must be set"),
            messages = messages?.map { it.asChatMessage() } ?: error("messages must be set"),
            temperature = temperature,
            topP = topP,
            n = n,
            stop = stop,
            maxTokens = maxTokens,
            presencePenalty = presencePenalty,
            frequencyPenalty = frequencyPenalty,
            logitBias = logitBias,
            user = user)

    @OptIn(BetaOpenAI::class)
    fun modifyUserMessageContent(content: String): OpenAIChatCompletionRequest {
        return builder {
            model?.also { model(model) }
            modelRequest?.also { modelRequest(modelRequest) }
            messages?.also { messages ->
                messages(messages.filter { it.role != ChatRole.User } +
                    OpenAIChatMessage(
                        content = content,
                        role = ChatRole.User
                    ))
            }
            temperature?.also { temperature(temperature) }
            topP?.also { topP(topP) }
            n?.also { n(n) }
            stop?.also { stop(stop) }
            maxTokens?.also { maxTokens(maxTokens) }
            presencePenalty?.also { presencePenalty(presencePenalty) }
            frequencyPenalty?.also { frequencyPenalty(frequencyPenalty) }
            logitBias?.also { logitBias(logitBias) }
            user?.also { user(user) }
        }.build()
    }

    override fun toString(): String =
        "OpenAIChatCompletionRequest(model=$model, modelRequest=$modelRequest, messages=$messages, " +
            "temperature=$temperature, topP=$topP, n=$n, stop=$stop, maxTokens=$maxTokens, " +
            "presencePenalty=$presencePenalty, frequencyPenalty=$frequencyPenalty, " +
            "logitBias=$logitBias, user=$user, optimize=$optimize)"

    companion object {
        fun builder(block: Builder.() -> Unit = {}) = Builder().apply(block)
    }

    class Builder : Cloneable {
        private var model: ModelId? = null
        private var modelRequest: OpenAIModelRequest? = null
        private var messages: List<OpenAIChatMessage>? = null
        private var temperature: Double? = null
        private var topP: Double? = null
        private var n: Int? = null
        private var stop: List<String>? = null
        private var maxTokens: Int? = null
        private var presencePenalty: Double? = null
        private var frequencyPenalty: Double? = null
        private var logitBias: Map<String, Int>? = null
        private var user: String? = null
        private var optimize: Boolean = false

        fun optimize(optimize: Boolean) = apply { this.optimize = optimize }

        fun modelRequest(modelRequest: OpenAIModelRequest) = apply { this.modelRequest = modelRequest }

        fun modelRequest(block: OpenAIModelRequest.Builder.() -> Unit) = apply {
            this.modelRequest = OpenAIModelRequest.Builder()
                .apply(block)
                .build()
        }

        fun model(model: ModelId) = apply { this.model = model }
        fun messages(messages: List<OpenAIChatMessage>) = apply { this.messages = messages }

        fun messages(block: OpenAIMessagesBuilder.() -> Unit) = apply {
            this.messages = OpenAIMessagesBuilder().apply(block)
                .build()
        }

        fun temperature(temperature: Double) = apply { this.temperature = temperature }
        fun topP(topP: Double) = apply { this.topP = topP }
        fun n(n: Int) = apply { this.n = n }
        fun stop(stop: List<String>) = apply { this.stop = stop }
        fun maxTokens(maxTokens: Int) = apply { this.maxTokens = maxTokens }
        fun presencePenalty(presencePenalty: Double) = apply { this.presencePenalty = presencePenalty }
        fun frequencyPenalty(frequencyPenalty: Double) = apply { this.frequencyPenalty = frequencyPenalty }
        fun logitBias(logitBias: Map<String, Int>) = apply { this.logitBias = logitBias }
        fun user(user: String) = apply { this.user = user }

        fun build() = OpenAIChatCompletionRequest(
            model = model,
            modelRequest = modelRequest,
            messages = messages,
            temperature = temperature,
            topP = topP,
            n = n,
            stop = stop,
            maxTokens = maxTokens,
            presencePenalty = presencePenalty,
            frequencyPenalty = frequencyPenalty,
            logitBias = logitBias,
            user = user,
            optimize = optimize
        )

        override fun toString(): String =
            "Builder(model=$model, modelRequest=$modelRequest, messages=$messages, temperature=$temperature, " +
                "topP=$topP, n=$n, stop=$stop, maxTokens=$maxTokens, presencePenalty=$presencePenalty, " +
                "frequencyPenalty=$frequencyPenalty, logitBias=$logitBias, user=$user, optimize=$optimize)"
    }
}