package net.doemges.kogniswarm.agent

import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.shell.ShellService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@KordPreview
@ExperimentalCoroutinesApi
@SpringBootTest
class AgentServiceIntegrationTest {
    @Value("\${discord.token}")
    private lateinit var botToken: String

    @Autowired
    private lateinit var shellService: ShellService

    @Autowired
    private lateinit var kord: Kord

    @Autowired
    private lateinit var agentService: AgentService

    private val responseCollector = mutableListOf<String>()

    class TestCommandQueue(kord: Kord, private val responseCollector: MutableList<String>) : DefaultCommandQueue(kord) {
        override suspend fun processMessageEvent(event: MessageCreateEvent) {
            super.processMessageEvent(event)
            val messageContent = event.message.content
            commandBuffer.add(messageContent)
        }
    }


    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            println("Setting up the test environment")
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            println("Tearing down the test environment")
        }
    }

    @Test
    fun testAgentReceivesAndAnswersMessage() = runBlocking {
        println("Starting the testAgentReceivesAndAnswersMessage test")

        // Create the test command queue
        val commandQueue = TestCommandQueue(kord, responseCollector)

        // Create the agent with the custom result handler
        val agent = agentService.createAgent()
                .apply {
                    initAgent(agentService, commandQueue = commandQueue)
                }

        println("Agent: $agent")

        val testMessageContent = "@${agent.id.name} help"
        val expectedResponse = "Expected response"


        // Fetch the list of channels from the server
        val channels = kord.guilds.first().channels.toList()
                .filterIsInstance<TextChannel>()

        println(
            "Channels: ${
                channels.mapNotNull { it.data.name.value }
                        .joinToString(", ")
            }"
        )

        // Use the first text channel as the test channel
        val channel = channels.firstOrNull() ?: throw IllegalStateException("No text channel found")

        println("Channel: ${channel.data.name.value}")

        println("Sending a message to the test channel")
        // Send a message to the test channel
        val message = channel.createMessage(testMessageContent)

        println("Message: '${message.data.content}'")

        // Wait for the agent to process the message
        println("Waiting for the agent to process the message")
        delay(5000)

        // Check if the agent received the message
        println("Checking if the agent received the message")
        val lastMessage =
            channel.getMessage(channel.lastMessageId ?: throw IllegalStateException("No last message found")).content

        println("Last message: '$lastMessage'")
        val agentResponse = responseCollector.joinToString("\n")
        println("Agent response: '$agentResponse'")
        assertEquals(expectedResponse, agentResponse, "The agent's response does not match the expected response")

        println("Test completed")
    }
}