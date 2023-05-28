package net.doemges.kogniswarm.action

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import net.doemges.kogniswarm.core.Mission

class UpdateActionHistoryProcessorTest {

    private lateinit var actionHistory: ActionHistory
    private lateinit var processor: UpdateActionHistoryProcessor

    @BeforeEach
    fun setUp() {
        actionHistory = mockk(relaxed = true)
        processor = UpdateActionHistoryProcessor(actionHistory)
    }

    @Test
    fun `should add action to history when action is present`() {
        // Given
        val action = Action(mockk(relaxed = true), mapOf())
        val mission = Mission("user", "agent", "prompt")
        val exchange = mockk<Exchange>()
        val message = mockk<Message>()
        every { exchange.getIn() } returns message
        every { message.body } returns mission
        every { message.headers } returns mapOf("action" to action)

        // When
        processor.process(exchange)

        // Then
        verify { actionHistory.put(mission, action) }
    }

    @Test
    fun `should not add action to history when action is not present`() {
        // Given
        val mission = Mission("user", "agent", "prompt")
        val exchange = mockk<Exchange>()
        val message = mockk<Message>()
        every { exchange.getIn() } returns message
        every { message.body } returns mission
        every { message.headers } returns emptyMap()

        // When
        processor.process(exchange)

        // Then
        verify(exactly = 0) { actionHistory.put(any(), any()) }
    }

    @Test
    fun `should correctly retrieve action history`() {
        // Given
        val mission = Mission("user", "agent", "prompt")
        val action = Action(mockk(relaxed = true), mapOf())
        every { actionHistory.get(mission) } returns listOf(action)

        // When
        val result = actionHistory.get(mission)

        // Then
        assertThat(result).isNotNull()
        assertThat(result!![0]).isEqualTo(action)
    }
}
