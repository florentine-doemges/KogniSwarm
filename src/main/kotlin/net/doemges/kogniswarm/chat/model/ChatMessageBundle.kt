package net.doemges.kogniswarm.chat.model

class ChatMessageBundle(vararg messages: ChatMessage?) : List<ChatMessage> by messages.filterNotNull().toList() {
    companion object {
        fun fromInput(input: String, systemInput: String? = null): ChatMessageBundle =
            ChatMessageBundle(
                ChatMessage(Role.USER, input),
                ChatMessage(Role.SYSTEM, systemInput).takeIf { systemInput != null }
            )
    }
}