package net.doemges.kogniswarm.action

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatMessage
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.doemges.kogniswarm.core.Mission
import net.doemges.kogniswarm.tool.Tool
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.support.DefaultExchange
import org.apache.camel.support.DefaultMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(BetaOpenAI::class)
class ActionSummaryProcessorTest {

    private lateinit var processor: ActionSummaryProcessor
    private val objectMapper: ObjectMapper = mockk(relaxed = true)
    private val camelContext: CamelContext = mockk(relaxed = true)
    private val producerTemplate: ProducerTemplate = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        processor = ActionSummaryProcessor(objectMapper, camelContext)
        every { camelContext.createProducerTemplate() } returns producerTemplate
    }

    @Test
    fun `process should modify action description`() {
        val action = Action(mockk<Tool>(relaxed = true), mapOf(), null, null)
        val mission = Mission("user1", "agent1", "userPrompt1")
        val exchange = DefaultExchange(camelContext).apply {
            `in` = DefaultMessage(camelContext).apply {
                headers["action"] = action
                body = mission
            }
        }
        val chatCompletion: ChatCompletion = mockk(relaxed = true)
        val completionMessage: ChatMessage = mockk(relaxed = true)
        val completionResponseSlot = slot<Any>()
        every { producerTemplate.requestBody("direct:openai-chatcompletion", capture(completionResponseSlot)) } returns chatCompletion
        every { chatCompletion.choices.first().message } returns completionMessage
        every { completionMessage.content } returns "New action description"

        processor.process(exchange)

        assertThat(action.description).isEqualTo("New action description")
        verify(exactly = 1) { producerTemplate.requestBody("direct:openai-chatcompletion", any()) }
        verify(exactly = 1) { objectMapper.writeValueAsString(action) }
    }
}
