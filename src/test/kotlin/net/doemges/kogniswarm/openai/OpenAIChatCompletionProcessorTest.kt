package net.doemges.kogniswarm.openai

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(BetaOpenAI::class)
class OpenAIChatCompletionProcessorTest {
    private val chatCompletionRequest: ChatCompletionRequest = mockk()
    private val openAIChatCompletionRequest: OpenAIChatCompletionRequest = mockk<OpenAIChatCompletionRequest>().apply {
        coEvery { asChatCompletionRequest(any()) } returns chatCompletionRequest
        every { messages } returns listOf(mockk<OpenAIChatMessage>().apply {
            every { content } returns "test content"
            every { role } returns ChatRole.User
        })
    }
    private val messageMock: Message = mockk<Message>().apply {
        every { body } returns openAIChatCompletionRequest
        every { body = any() } just Runs
    }
    private val exchange: Exchange = mockk<Exchange>().apply {
        every { getIn() } returns messageMock
        every { message } returns messageMock
    }
    private val chatChoice: ChatChoice = mockk<ChatChoice>().apply {
        every { message } returns mockk<ChatMessage>().apply {
            every { content } returns "test content"
        }
    }
    private val chatCompletion: ChatCompletion = mockk<ChatCompletion>().apply {
        every { choices } returns listOf(chatChoice)
    }
    private val openAI: OpenAI = mockk<OpenAI>().apply {
        coEvery { chatCompletion(chatCompletionRequest) } returns chatCompletion
    }
    private lateinit var processor: OpenAIChatCompletionProcessor

    @BeforeEach
    fun setup() {
        processor = OpenAIChatCompletionProcessor(openAI, mockk<ObjectMapper>().apply {
            every { writeValueAsString(any()) } returns "test"
        })
    }

    @Test
    fun `should process exchange successfully`() = runBlocking {
        processor.processSuspend(exchange)

        verify { messageMock.body = chatCompletion }
    }

    @Test
    fun `should return the correct message content`(): Unit = runBlocking {
        processor.processSuspend(exchange)

        assertThat(chatChoice.message?.content).isEqualTo("test content")
    }
}
