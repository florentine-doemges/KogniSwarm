package net.doemges.kogniswarm.assistant

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.doemges.kogniswarm.assistant.model.AssistantRequest
import net.doemges.kogniswarm.assistant.model.AssistantResponse
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.junit.jupiter.api.Test

class AssistantProcessorTest {

    @Test
    fun `test AssistantProcessor processes request and returns response`() {
        // Prepare
        val chatService = mockk<ChatService>().apply {
            every { sendToChatGpt(any()) } returns "Hi! How can I help you?"
        }
        val assistantProcessor = AssistantProcessor(chatService)
        val input = "Hello, assistant!"
        val expectedResponseText = "Hi! How can I help you?"

        every { chatService.sendToChatGpt(ChatMessageBundle.fromInput(input)) } returns expectedResponseText

        // Execute
        val response = assistantProcessor.processRequest(AssistantRequest(input,""))

        // Verify
        verify { chatService.sendToChatGpt(any()) }
        assertThat(response).isEqualTo(AssistantResponse(expectedResponseText))
    }

}
