package net.doemges.kogniswarm.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import org.slf4j.LoggerFactory

@OptIn(BetaOpenAI::class)
class OpenAIChatMessage(
    val role: ChatRole? = null,
    val content: String? = null,
    val name: String? = null
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun asChatMessage(): ChatMessage = ChatMessage(role ?: ChatRole.User, content ?: error("content must be set"), name).also{
        logger.info("Created ChatMessage: $it")
    }

    companion object {
        fun builder(block: Builder.() -> Unit = {}) = Builder().apply(block)
    }

    class Builder {
        private var role: ChatRole? = null
        private var content: String? = null
        private var name: String? = null

        fun role(role: ChatRole) = apply { this.role = role }
        fun content(content: String) = apply { this.content = content }
        fun name(name: String) = apply { this.name = name }

        fun build() = OpenAIChatMessage(
            role = role,
            content = content,
            name = name
        )
    }
}