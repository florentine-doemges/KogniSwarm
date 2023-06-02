package net.doemges.kogniswarm.openai.model

import com.aallam.openai.api.BetaOpenAI

class OpenAIPromptBuilder : OpenAIMessagesBuilder() {


    private val variables: MutableMap<String, String> = mutableMapOf()
    private val templateMessages: MutableList<OpenAIChatMessage> = mutableListOf()

    @OptIn(BetaOpenAI::class)
    override fun build(): List<OpenAIChatMessage> {
        templateMessages.forEach { message ->
            val content = message.content?.let { content ->
                var out = content
                variables.forEach { (key, replacement) ->
                    while (out.contains("{$key}"))
                        out = out.replace("{$key}", replacement)
                }
                out
            }
            message(OpenAIChatMessage(message.role, content, message.name))
        }
        return super.build()
    }

    fun template(function: OpenAIMessagesBuilder.() -> Unit) {
        OpenAIMessagesBuilder().apply(function)
            .build()
            .forEach { templateMessages.add(it) }
    }

    fun variable(key: String, replacement: String) {
        variables[key] = replacement
    }

}