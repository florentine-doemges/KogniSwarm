package net.doemges.kogniswarm.agent

import assertk.assertThat
import assertk.assertions.contains
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.entity.Message
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import net.doemges.kogniswarm.data.Fixtures
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AgentTest {

    private val fixture = Fixtures.fixtureWithFaker()

    @Test
    fun `Agent should add message to messageQueue when message is received`() {
        val message = mockk<Message>()
        val event = mockk<MessageCreateEvent> {
            every { this@mockk.message } returns message
        }

        val scope = TestScope()
        val eventsChannel = Channel<Event>()
        val kord = mockk<Kord> {
            coEvery { events } returns eventsChannel.receiveAsFlow().shareIn(scope, SharingStarted.Eagerly)
        }

        val agent = Agent(scope, fixture(), kord)

        // Simulate the receipt of a message
        scope.launch {
            eventsChannel.send(event)
        }

        // Wait for the message to be processed
        scope.advanceUntilIdle()

        assertThat(agent.messageQueue).contains(message)
    }
}
