package net.doemges.kogniswarm.agent

import kotlinx.coroutines.channels.Channel
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import net.doemges.kogniswarm.memory.Memento
import net.doemges.kogniswarm.memory.Memory
import net.doemges.kogniswarm.shell.ShellTask
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Agent(
    val id: AgentIdentifier,
    private val chatGptChannel: Channel<Request<String>>,
    private val shellChannel: Channel<Request<ShellTask>>,
    private val memory: Memory<String>
) {

    private val logger: Logger = LoggerFactory.getLogger(Agent::class.java)

    suspend fun processInput(
        input: String,
        message: Message
    ): String = Channel<Response<String>>().let { responseChannel ->
        logger.info("Processing input: $input")
        commitToMemory(message, input)
        chatGptChannel.send(Request(input, responseChannel))
        val msg = responseChannel.receive().message
        val result = "Agent ${id.name}: $msg"
        commitToMemory(message, msg)
        logger.info(result)
        result
    }

    private fun commitToMemory(message: Message, msg: String) {
        val memento = Memento(agentId = id.id.toString(), authorId = message.author.name, content = msg)
        memory.commit(memento)
    }

}

