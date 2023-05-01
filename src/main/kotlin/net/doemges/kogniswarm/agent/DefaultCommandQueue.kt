package net.doemges.kogniswarm.agent

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent

open class DefaultCommandQueue(private val kord: Kord) : CommandQueue {
    private val queue: ArrayDeque<MessageTask> = ArrayDeque()
    protected val commandBuffer: MutableList<String> = mutableListOf()

    // Process incoming message events
    override suspend fun processMessageEvent(event: MessageCreateEvent) {
        println("Processing message event: $event")
        val messageTask = MessageTask(event, kord)
        queue.addLast(messageTask)
        println("Message task added to queue: $messageTask")
    }

    // Get the next message task from the queue
    override fun getNextMessageTask(): MessageTask? {
        val messageTask = queue.removeFirstOrNull()
        println("Getting next message task: $messageTask")
        return messageTask
    }

    // Get the next command from the command buffer, or process the next message task if the buffer is empty
    override suspend fun getNextCommand(): String {
        // If the command buffer is not empty, return the next command
        commandBuffer.removeFirstOrNull()?.let { command ->
            println("Getting command from buffer: $command")
            return command
        }

        // If the command buffer is empty, process the next message task
        getNextMessageTask()?.let { messageTask ->
            messageTask.event.message.content.split("\n").let { lines ->
                println("Splitting message content into lines: $lines")
                commandBuffer.addAll(lines)
            }

            commandBuffer.removeFirstOrNull()?.let { command ->
                println("Getting command from buffer after processing message task: $command")
                return command
            }
        }

        // If there are no commands and no message tasks, return an empty string
        return ""
    }
}
