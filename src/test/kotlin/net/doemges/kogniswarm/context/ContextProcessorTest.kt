package net.doemges.kogniswarm.context

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import net.doemges.kogniswarm.context.processor.ContextProcessor
import net.doemges.kogniswarm.context.service.MemoryContextService
import net.doemges.kogniswarm.core.model.Mission
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(BetaOpenAI::class)
class ContextProcessorTest {

    private lateinit var memoryContextService: MemoryContextService
    private lateinit var camelContext: CamelContext
    private lateinit var processor: Processor
    private lateinit var exchange: Exchange
    private lateinit var message: Message

    @BeforeEach
    fun setUp() {
        memoryContextService = mockk<MemoryContextService>().apply {
            every { get(any(), any(), any()) } returns mockk<ArrayList<Map<String, String>>>().apply {
                every { isEmpty() } returns false
            }
            every { put(any(), any()) } just Runs
            every { formatContext(any(), any()) } returns "post-context"
        }
        camelContext = mockk()
        exchange = mockk()
        message = mockk()
        processor = ContextProcessor(memoryContextService, camelContext)

        every { exchange.getIn() } returns message
    }

    @Test
    fun `process should update exchange's context header with chat completion response content`() {
        val mission = Mission("user", "agentName", "userPrompt")
        val preContext = "pre-context"
        val postContext = "post-context"
        val producerTemplate = mockk<ProducerTemplate>()
        val chatChoice = mockk<ChatChoice>()
        val chatCompletion = mockk<ChatCompletion>()
        val chatMessage = ChatMessage(ChatRole.System, postContext)
        val headers = mutableMapOf<String, Any?>()

        every { message.body } returns mission
        every { camelContext.createProducerTemplate() } returns producerTemplate
        every { producerTemplate.requestBody(any<String>(), any<Any>()) } returns chatCompletion
        every { chatCompletion.choices } returns listOf(chatChoice)
        every { chatChoice.message } returns chatMessage
        every { message.headers } returns headers

        processor.process(exchange)

        assertThat(headers["context"]).isEqualTo(postContext)
    }


}
