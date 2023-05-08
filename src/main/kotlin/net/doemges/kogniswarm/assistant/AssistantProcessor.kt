package net.doemges.kogniswarm.assistant

import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import net.doemges.kogniswarm.io.OutputProcessor

class AssistantProcessor(private val chatService: ChatService) : OutputProcessor<AssistantRequest, AssistantResponse> {
    override fun processRequest(payload: AssistantRequest): AssistantResponse =
        AssistantResponse(chatService.sendToChatGpt(ChatMessageBundle.fromInput(payload.input)))
}
