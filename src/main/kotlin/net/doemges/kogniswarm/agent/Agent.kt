package net.doemges.kogniswarm.agent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.shell.ResultHandler

class Agent(
    val scope: CoroutineScope,
    agentCreator: AgentService,
    commandQueue: CommandQueue = DefaultCommandQueue(agentCreator.kord),
    private val customResultHandler: ResultHandler<Any>? = null
) : CoroutineScope by scope {

    private lateinit var shellOperator: ShellOperator
    private lateinit var discordEventDispatcher: DiscordEventDispatcher
    val id: AgentIdentifier = agentCreator.agentIdentifierGenerator()

    init {
        initAgent(agentCreator, commandQueue)
        scope.launch {
            shellOperator.start()
        }
    }

    fun initAgent(agentCreator: AgentService, commandQueue: CommandQueue) {
        shellOperator = DefaultShellOperator(
            agentCreator.shellService,
            this,
            commandQueue,
            customResultHandler
        )
        discordEventDispatcher = DefaultDiscordEventDispatcher(agentCreator.kord, this, commandQueue)
        discordEventDispatcher.setId(id)
    }

    override fun toString(): String = "Agent(id=$id)"
}