
package net.doemges.kogniswarm.chat

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.doemges.kogniswarm.chat.model.ChatCompletionRequest
import net.doemges.kogniswarm.chat.model.ChatCompletionResponse
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@OptIn(ExperimentalCoroutinesApi::class)
class ChatServiceTest {

    private val restTemplate = mockk<RestTemplate>()
    private val chatCompletionRequestFactory = mockk<ChatCompletionRequestFactory>()

    private lateinit var chatService: ChatService

    private val fixture = kotlinFixture {
        javaFakerStrategy()

        // Add a custom factory for ChatCompletionRequest
        factory<ChatCompletionRequest> {
            ChatCompletionRequest(
                messages = emptyList(),
                temperature = 0.8,
                maxTokens = 100,
                n = 1,
                topP = 0.9,
                frequencyPenalty = 0.0,
                presencePenalty = 0.0,
                model = "gpt-4"
            )
        }
    }


    @BeforeEach
    fun setUp() {
        chatService = ChatService(
            restTemplate,
            chatCompletionRequestFactory,
            mockk(),
            "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"
        )
    }


    @Test
    fun `sendToChatGpt with ChatMessageBundle should return the correct response`() = runTest {
        // Prepare mock data
        val chatMessageBundle = fixture<ChatMessageBundle>()
        val chatCompletionRequest = fixture<ChatCompletionRequest>()
        val chatCompletionResponse = fixture<ChatCompletionResponse>()
        val responseEntity = ResponseEntity(chatCompletionResponse, HttpStatus.OK)

        // Configure mock behaviors
        every { chatCompletionRequestFactory.createChatCompletionRequest(chatMessageBundle) } returns chatCompletionRequest
        val urlSlot = slot<String>()
        val requestSlot = slot<ChatCompletionRequestWrapper>()
        coEvery {
            restTemplate.exchange(
                capture(urlSlot),
                eq(HttpMethod.POST),
                capture(requestSlot),
                eq(ChatCompletionResponse::class.java)
            )
        } returns responseEntity

        // Call the function under test
        val response = chatService.sendToChatGpt(chatMessageBundle)

        // Verify the response
        assertThat(response).isEqualTo(chatCompletionResponse.choices[0].message.content)
    }


}




