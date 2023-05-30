package net.doemges.kogniswarm.context

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.Mission
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class UpdateContextProcessorTest {

    private val mockMemoryContext: MemoryContext = mockk()
    private val mockExchange: Exchange = mockk()
    private val mockInMessage: Message = mockk()
    private val mission: Mission = Mission("user", "agentName", "userPrompt")
    private lateinit var updateContextProcessor: UpdateContextProcessor

    @BeforeEach
    fun setUp() {
        every { mockExchange.getIn() } returns mockInMessage
        every { mockInMessage.body } returns mission
        updateContextProcessor = UpdateContextProcessor(mockMemoryContext)
    }

    @Test
    fun `process - when action is not null - should add to context`() {
        // Arrange
        val actionSlot = slot<Action>()
        val expectedAction = Action(mockk(), mapOf("key" to "value"))
        every { mockInMessage.headers } returns mapOf("action" to expectedAction)
        every { mockMemoryContext.put(any(), any(), any()) } just Runs

        println(mockInMessage.headers)
        // Act
        updateContextProcessor.process(mockExchange)

        // Assert
        verify(exactly = 1) { mockMemoryContext.put(mission, any(), any()) }
    }


    @Test
    fun `process - when action is null - should not add to context`() {
        // Arrange
        every { mockInMessage.headers } returns mapOf<String, Any?>()

        // Act
        updateContextProcessor.process(mockExchange)

        // Assert
        verify(exactly = 0) { mockMemoryContext.put(mission, any()) }
    }

}
