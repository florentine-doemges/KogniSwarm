package net.doemges.kogniswarm.openai

open class OpenAIMessagesBuilder {
    private val messages: MutableList<OpenAIChatMessage> = mutableListOf()
    fun message(block: OpenAIChatMessage.Builder.() -> Unit) = apply {
        messages.add(
            OpenAIChatMessage
                .builder(block)
                .build()
        )
    }

    fun message(message: OpenAIChatMessage) = apply {
        messages.add(message)
    }

    open fun build(): List<OpenAIChatMessage> = messages
    fun prompt(block: OpenAIPromptBuilder.() -> Unit) = apply {
        messages.addAll(
            OpenAIPromptBuilder()
                .apply(block)
                .build()
        )
    }

}