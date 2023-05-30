package net.doemges.kogniswarm.context

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import io.mockk.every
import io.mockk.mockk
import net.doemges.kogniswarm.core.Mission
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(BetaOpenAI::class)
class ContextProcessorTest {

    private lateinit var memoryContext: MemoryContext
    private lateinit var camelContext: CamelContext
    private lateinit var processor: Processor
    private lateinit var exchange: Exchange
    private lateinit var message: Message

    @BeforeEach
    fun setUp() {
        memoryContext = mockk(relaxed = true)
        camelContext = mockk(relaxed = true)
        exchange = mockk(relaxed = true)
        message = mockk(relaxed = true)
        processor = ContextProcessor(memoryContext, camelContext)

        every { exchange.getIn() } returns message
    }

    @Test
    fun `process should update exchange's context header with chat completion response content`() {
        val mission = Mission("user", "agentName", "userPrompt")
        val preContext = "pre-context"
        val postContext = "post-context"
        val producerTemplate = mockk<ProducerTemplate>(relaxed = true)
        val chatChoice = mockk<ChatChoice>(relaxed = true)
        val chatCompletion = mockk<ChatCompletion>(relaxed = true)
        val chatMessage = ChatMessage(ChatRole.System, postContext)
        val headers = mutableMapOf<String, Any?>()

        every { message.body } returns mission
        every { memoryContext.get(mission,) } returns preContext
        every { camelContext.createProducerTemplate() } returns producerTemplate
        every { producerTemplate.requestBody(any<String>(), any<Any>()) } returns chatCompletion
        every { chatCompletion.choices } returns listOf(chatChoice)
        every { chatChoice.message } returns chatMessage
        every { message.headers } returns headers

        processor.process(exchange)

        assertThat(headers["context"]).isEqualTo(postContext)
    }


}
