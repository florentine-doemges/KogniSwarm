package net.doemges.kogniswarm.agent

import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.shell.ShellService
import org.springframework.shell.Input
import org.springframework.shell.ResultHandler
import org.springframework.shell.Shell

open class DefaultShellOperator(
    private val shellService: ShellService,
    agent: Agent,
    private val commandQueue: CommandQueue,
    override val customResultHandler: ResultHandler<Any>? = null
) : ShellOperator {
    private var id: AgentIdentifier = agent.id
    lateinit var shell: Shell

    override suspend fun start() {
        println("Starting shell: (${id.name})") // Debug statement
        shell = shellService.createShell(this, customResultHandler)
        shell.run(this)
        println("Shell started: (${id.name})") // Debug statement
    }

    override fun readInput(): Input {
        val inputString = runBlocking {
            val nextCommand = commandQueue.getNextCommand()
            println("Next command: $nextCommand") // Debug statement
            nextCommand
        }.substringAfter("@${id.name} ")
        println("Reading input (${id.name}): $inputString") // Debug statement
        return Input { inputString }
    }

    override fun handleResult(result: Any?) {
        println("Handling result (${id.name}): $result") // Debug statement
        runBlocking {
            commandQueue.getNextMessageTask()
                    ?.handleResult(result)
        }
        println("Result handled: (${id.name})") // Debug statement
    }
}
