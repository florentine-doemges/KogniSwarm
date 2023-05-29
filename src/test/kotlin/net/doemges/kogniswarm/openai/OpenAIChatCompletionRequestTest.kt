package net.doemges.kogniswarm.openai

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import assertk.assertions.prop
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test


@OptIn(BetaOpenAI::class)
class OpenAIChatCompletionRequestTest {

    companion object {
        private const val TEST = "test"
        private const val TEST_CONTENT = "test content"
        private const val TEST_USER = "test user"
        private const val TEST_STOP = "test stop"

        private const val MAX_TOKENS = 10
        private const val TEMPERATURE = 0.5
        private const val PRESENCE_PENALTY = 0.5
        private const val FREQUENCE_PENALTY = 0.5
        private const val LOGIT_BIAS = 1
        private const val N = 1
    }

    @Test
    fun testAsChatCompletionRequestWithFixedModel() = runBlocking {
        val openAI: OpenAI = mockk()
        val model = ModelId("gpt-3.5-turbo")
        assertThat {
            createChatCompletionRequest {
                model(model)
            }.asChatCompletionRequest(openAI)
        }
            .isSuccess()
            .all {
                prop("model") { it.model }.isEqualTo(model)
                assertStandard()
            }
    }

    @Test
    fun testAsChatCompletionRequestWithModelRequest() = runBlocking {
        val openAI: OpenAI = mockk<OpenAI>().apply {
            coEvery { models() } returns listOf(
                mockk {
                    every { id } returns ModelId("gpt-3.5-turbo")
                    every { permission } returns listOf(mockk())
                },
            )
        }
        assertThat {
            createChatCompletionRequest {
                modelRequest {
                    modelName("gpt")
                    isGPT4allowed(false)
                }
            }.asChatCompletionRequest(openAI)
        }
            .isSuccess()
            .all {
                prop("model") { it.model }
                    .prop("id") { it.id }
                    .contains("gpt")
                assertStandard()
            }
    }

    @Test
    fun testAsChatCompletionRequestWithModelRequest2() = runBlocking {
        val openAI: OpenAI = mockk<OpenAI>().apply {
            coEvery { models() } returns listOf(
                mockk {
                    every { id } returns ModelId("gpt-3.5-turbo")
                    every { permission } returns listOf(mockk())
                },
            )
        }
        assertThat {
            createChatCompletionRequest {
                modelRequest(OpenAIModelRequest.builder {
                    modelName("gpt")
                    isGPT4allowed(false)
                }
                    .build())
            }.asChatCompletionRequest(openAI)
        }
            .isSuccess()
            .all {
                prop("model") { it.model }
                    .prop("id") { it.id }
                    .contains("gpt")
                assertStandard()
            }
    }

    private fun createChatCompletionRequest(builderAction: OpenAIChatCompletionRequest.Builder.() -> Unit): OpenAIChatCompletionRequest {
        val messages = listOf(OpenAIChatMessage.builder {
            setTestContent()
        }
            .build())
        return OpenAIChatCompletionRequest.builder {
            builderAction()
            messages {
                message {
                    setTestContent()
                }
                message(OpenAIChatMessage(ChatRole.User, TEST_CONTENT, TEST_USER))
            }
            messages(messages)
            maxTokens(MAX_TOKENS)
            stop(listOf(TEST_STOP))
            temperature(TEMPERATURE)
            topP(1.0)
            presencePenalty(PRESENCE_PENALTY)
            frequencyPenalty(FREQUENCE_PENALTY)
            logitBias(mapOf(TEST to LOGIT_BIAS))
            user(TEST_USER)
            n(N)
        }
            .build()
    }

    private fun Assert<ChatCompletionRequest>.assertStandard() {
        prop("maxTokens") { it.maxTokens }.isEqualTo(MAX_TOKENS)
        prop("stop") { it.stop }.isEqualTo(listOf(TEST_STOP))
        prop("temperature") { it.temperature }.isEqualTo(TEMPERATURE)
        prop("topP") { it.topP }.isEqualTo(1.0)
        prop("presencePenalty") { it.presencePenalty }.isEqualTo(PRESENCE_PENALTY)
        prop("frequencyPenalty") { it.frequencyPenalty }.isEqualTo(FREQUENCE_PENALTY)
        prop("logitBias") { it.logitBias }.isEqualTo(mapOf(TEST to LOGIT_BIAS))
        prop("user") { it.user }.isEqualTo(TEST_USER)
        prop("messages") { it.messages }.all {
            prop("size") { it.size }.isEqualTo(LOGIT_BIAS)
            prop("first") { it.first() }.all {
                prop("role") { it.role }.isEqualTo(ChatRole.User)
                prop("content") { it.content }.isEqualTo(TEST_CONTENT)
                prop("name") { it.name }.isEqualTo(TEST_USER)
            }
            prop("last") { it.last() }.all {
                prop("role") { it.role }.isEqualTo(ChatRole.User)
                prop("content") { it.content }.isEqualTo(TEST_CONTENT)
                prop("name") { it.name }.isEqualTo(TEST_USER)
            }
        }
        prop("n") { it.n }.isEqualTo(N)
    }

    private fun OpenAIChatMessage.Builder.setTestContent() {
        role(ChatRole.User)
        content(TEST_CONTENT)
        name(TEST_USER)
    }
}
