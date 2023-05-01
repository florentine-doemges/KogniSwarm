package net.doemges.kogniswarm.agent

import dev.kord.core.Kord
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.shell.ShellService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AgentTest {

    private lateinit var agentService: AgentService
    private lateinit var commandQueue: CommandQueue

    @BeforeEach
    fun setUp() {
        val shellService = mockk<ShellService>()
        val kord = mockk<Kord>().apply {
            coEvery { events } returns mockk()
        }
        agentService = AgentService(shellService, kord)
        commandQueue = DefaultCommandQueue(kord)
    }

    @Test
    fun `Agent_init_initsShellOperatorAndDiscordEventDispatcher`() = runBlocking {
        // Arrange
        val testCoroutineScope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)

        // Act
        val agent = Agent(testCoroutineScope, agentService, commandQueue)

        // Assert
        // Add assertions to verify that the ShellOperator and DiscordEventDispatcher instances are initialized as expected
    }
}
