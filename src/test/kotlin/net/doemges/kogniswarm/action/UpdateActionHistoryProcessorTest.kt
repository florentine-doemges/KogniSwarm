package net.doemges.kogniswarm.action

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.action.processor.UpdateActionHistoryProcessor
import net.doemges.kogniswarm.action.service.ActionHistoryService
import net.doemges.kogniswarm.mission.model.MissionKey
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateActionHistoryProcessorTest {

    private lateinit var actionHistoryService: ActionHistoryService
    private lateinit var processor: UpdateActionHistoryProcessor

    @BeforeEach
    fun setUp() {
        actionHistoryService = mockk(relaxed = true)
        processor = UpdateActionHistoryProcessor(actionHistoryService)
    }

    @Test
    fun `should add action to history when action is present`() {
        // Given
        val action = Action(mockk(relaxed = true), mapOf())
        val missionKey = MissionKey("user", "agent", "prompt")
        val exchange = mockk<Exchange>()
        val message = mockk<Message>()
        every { exchange.getIn() } returns message
        every { message.body } returns missionKey
        every { message.headers } returns mapOf("action" to action)

        // When
        processor.process(exchange)

        // Then
        verify { actionHistoryService.put(missionKey, action) }
    }

    @Test
    fun `should not add action to history when action is not present`() {
        // Given
        val missionKey = MissionKey("user", "agent", "prompt")
        val exchange = mockk<Exchange>()
        val message = mockk<Message>()
        every { exchange.getIn() } returns message
        every { message.body } returns missionKey
        every { message.headers } returns emptyMap()

        // When
        processor.process(exchange)

        // Then
        verify(exactly = 0) { actionHistoryService.put(any(), any()) }
    }

    @Test
    fun `should correctly retrieve action history`() {
        // Given
        val missionKey = MissionKey("user", "agent", "prompt")
        val action = Action(mockk(relaxed = true), mapOf())
        every { actionHistoryService.get(missionKey) } returns listOf(action)

        // When
        val result = actionHistoryService.get(missionKey)

        // Then
        assertThat(result).isNotNull()
        assertThat(result!![0]).isEqualTo(action)
    }
}
