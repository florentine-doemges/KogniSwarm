package net.doemges.kogniswarm.agent

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import dev.kord.core.Kord
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.shell.ShellService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AgentServiceTest {

    private lateinit var shellService: ShellService
    private lateinit var kord: Kord
    private lateinit var agentService: AgentService

    @BeforeEach
    fun setUp() {
        shellService = mockk<ShellService>().apply {
            coEvery { createShell<Any>(any()) } returns mockk()
        }
        kord = mockk<Kord>().apply {
            coEvery { events } returns mockk()
        }
        agentService = AgentService(shellService, kord)
    }

    @Test
    fun `createAgent_withValidScope_createsAgent`() = runBlocking {
        // Arrange
        val testCoroutineScope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO)

        // Act
        val agent = agentService.createAgent(testCoroutineScope,)

        // Assert
        assertThat(agent).isNotNull()
        assertThat(agent.scope).isEqualTo(testCoroutineScope)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `createAgent_withoutScope_usesDefaultScope`() = runBlocking {
        // Arrange

        // Act
        val agent = agentService.createAgent()

        // Assert
        assertThat(agent).isNotNull()
        assertThat(agent.scope.coroutineContext[CoroutineDispatcher]).isEqualTo(Dispatchers.IO)
    }

}
