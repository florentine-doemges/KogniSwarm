package net.doemges.kogniswarm.action

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.action.processor.ActionHistoryProcessor
import net.doemges.kogniswarm.action.service.ActionHistoryService
import net.doemges.kogniswarm.core.model.Mission
import net.doemges.kogniswarm.token.util.Tokenizer
import net.doemges.kogniswarm.token.service.TokenizerService
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ActionHistoryProcessorTest {

    private lateinit var actionHistoryService: ActionHistoryService
    private lateinit var exchange: Exchange
    private lateinit var inMessage: Message
    private lateinit var processor: ActionHistoryProcessor

    @BeforeEach
    fun setup() {
        actionHistoryService = mockk()
        exchange = mockk()
        inMessage = mockk()

        every { exchange.getIn() } returns inMessage

        val tokenizerService = mockk<TokenizerService>().apply {
            every { tokenizer } returns mockk<Tokenizer>().apply {
                every { tokenize(any()) } returns listOf("action")
            }
        }
        val objectMapper = mockk<ObjectMapper>().apply {
            every { writeValueAsString(any()) } returns "action"
        }
        processor = ActionHistoryProcessor(actionHistoryService, objectMapper, tokenizerService)
    }

    @Test
    fun `process should add history to exchange headers`() {
        val mission = Mission("user1", "agent1", "userPrompt1")
        val history = listOf(Action(mockk(), mapOf("arg1" to "val1")))

        every { inMessage.body } returns mission
        every { actionHistoryService.get(mission) } returns history

        val headersMap = mutableMapOf<String, Any>()
        every { inMessage.headers } returns headersMap

        processor.process(exchange)

        verify { actionHistoryService.get(mission) }
        verify { inMessage.headers }

        assertThat(headersMap["actionHistory"]).isEqualTo(history)
    }
}
