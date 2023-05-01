package net.doemges.kogniswarm.agent

import dev.kord.core.Kord
import kotlinx.coroutines.*
import net.doemges.kogniswarm.data.Fixtures
import net.doemges.kogniswarm.shell.ShellService
import org.springframework.shell.ResultHandler
import org.springframework.stereotype.Service

@Service
class AgentService(val shellService: ShellService, val kord: Kord) {
    val agentIdentifierGenerator = Fixtures.fixtureWithFaker()
    suspend fun createAgent(
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        customResultHandler: ResultHandler<Any>? = null
    ): Agent =
        Agent(
            scope = scope,
            agentCreator = this,
            customResultHandler = customResultHandler
        )
}

