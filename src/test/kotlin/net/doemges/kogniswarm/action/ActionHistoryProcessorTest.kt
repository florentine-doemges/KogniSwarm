package net.doemges.kogniswarm.action

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import net.doemges.kogniswarm.core.Mission

class ActionHistoryProcessorTest {

    private lateinit var actionHistory: ActionHistory
    private lateinit var exchange: Exchange
    private lateinit var inMessage: Message
    private lateinit var processor: ActionHistoryProcessor

    @BeforeEach
    fun setup() {
        actionHistory = mockk()
        exchange = mockk()
        inMessage = mockk()

        every { exchange.getIn() } returns inMessage

        processor = ActionHistoryProcessor(actionHistory)
    }

    @Test
    fun `process should add history to exchange headers`() {
        val mission = Mission("user1", "agent1", "userPrompt1")
        val history = listOf(Action(mockk(), mapOf("arg1" to "val1")))

        every { inMessage.body } returns mission
        every { actionHistory.get(mission) } returns history

        val headersMap = mutableMapOf<String, Any>()
        every { inMessage.headers } returns headersMap

        processor.process(exchange)

        verify { actionHistory.get(mission) }
        verify { inMessage.headers }

        assertThat(headersMap["actionHistory"]).isEqualTo(history)
    }
}
