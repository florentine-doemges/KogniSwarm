package net.doemges.kogniswarm.think

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.key
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatMessage
import io.mockk.every
import io.mockk.mockk
import net.doemges.kogniswarm.mission.model.MissionKey
import net.doemges.kogniswarm.think.processor.EndOfActionDecisionProcessor
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.ProducerTemplate
import org.junit.jupiter.api.Test

@OptIn(BetaOpenAI::class)
class EndOfActionDecisionProcessorTest {
    @Test
    fun testProcess() {
        val camelContext = mockk<CamelContext>().apply {
            every { createProducerTemplate() } returns mockk<ProducerTemplate>().apply {
                every { requestBody(any<String>(), any()) } returns mockk<ChatCompletion>().apply {
                    every { choices } returns listOf(mockk<ChatChoice>().apply {
                        every { message } returns mockk<ChatMessage>().apply {
                            every { content } returns "Test"
                        }
                    })
                }
            }
        }
        val endOfActionDecisionProcessor =
            EndOfActionDecisionProcessor(camelContext, mockk())

        val message = mockk<Message>().apply {
            every { body } returns MissionKey("Test", "Test", "Test")
            every { headers } returns mutableMapOf<String, Any>()
        }
        endOfActionDecisionProcessor.process(mockk<Exchange>().apply {
            every { getIn() } returns message
        })

        assertThat(message.headers).all {
            key("shouldContinue").all {
                isNotNull()
                isEqualTo(true)
            }
        }

    }
}
