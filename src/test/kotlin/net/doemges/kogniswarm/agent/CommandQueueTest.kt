package net.doemges.kogniswarm.agent

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommandQueueTest {

    private lateinit var kord: Kord
    private lateinit var commandQueue: CommandQueue

    @BeforeEach
    fun setUp() {
        kord = mockk<Kord>()
        commandQueue = DefaultCommandQueue(kord)
    }

    @Test
    fun `processMessageEvent_withMessageEvent_processesEvent`() = runBlocking {
        // Arrange
        val messageCreateEvent = mockk<MessageCreateEvent>()

        // Act
        commandQueue.processMessageEvent(messageCreateEvent)

        // Assert
        // Add assertions to verify that the message event has been processed as expected
    }

    @Test
    fun `getNextMessageTask_withoutMessages_returnsNull`() {
        // Arrange

        // Act
        val messageTask = commandQueue.getNextMessageTask()

        // Assert
        assertThat(messageTask).isNull()
    }

    @Test
    fun `getNextCommand_withoutCommands_returnsEmptyString`() = runBlocking {
        // Arrange

        // Act
        val command = commandQueue.getNextCommand()

        assertThat(command).isEqualTo("")
    }
}

