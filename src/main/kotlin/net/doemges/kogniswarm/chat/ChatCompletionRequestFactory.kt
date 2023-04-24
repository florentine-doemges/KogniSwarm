package net.doemges.kogniswarm.chat

import net.doemges.kogniswarm.chat.model.ChatCompletionRequest
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.springframework.stereotype.Component

@Component
class ChatCompletionRequestFactory {

    fun createChatCompletionRequest(
        bundle: ChatMessageBundle
    ): ChatCompletionRequest = ChatCompletionRequest(
        model = "gpt-3.5-turbo",
        messages = bundle
    )
}
