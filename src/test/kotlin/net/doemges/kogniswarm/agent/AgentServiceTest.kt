package net.doemges.kogniswarm.agent

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.discord.DiscordService
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import net.doemges.kogniswarm.memory.MemoryService
import net.doemges.kogniswarm.memory.MementoRepository
import net.doemges.kogniswarm.shell.ShellTask
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class AgentServiceTest(
    @Autowired private val agentService: AgentService,
    @Autowired private val discordService: DiscordService,
    @Autowired private val memoryService: MemoryService,
    @Autowired private val mementoRepository: MementoRepository,
    @Autowired private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(AgentServiceTest::class.java)
    @Suppress("UNCHECKED_CAST")
    @Test
    fun `AgentService should process input and save memory`() {
        // Given
        val shellChannel = Channel<Request<ShellTask>>()
        val chatGptChannel = Channel<Request<String>>()

        val testAgentIdentifier = AgentIdentifier(UUID.randomUUID(), "TestAgent")
        val testAgent = Agent(
            testAgentIdentifier,
            chatGptChannel,
            memoryService.createMemory(testAgentIdentifier.id.toString())
        )

        // Add the testAgent to the agents map in AgentService
        runBlocking {
            val agentsField = AgentService::class.java.getDeclaredField("agents")
            agentsField.isAccessible = true
            val agents = agentsField.get(agentService) as MutableMap<String, Agent>
            agents[testAgent.id.name] = testAgent
        }

        // Simulate AgentService receiving input
        runBlocking {
            launch {
                val message = "Hello TestAgent"
                discordService.sendMessage("@${testAgent.id.name} $message")
            }

            // Simulate chatGptChannel response
            launch {
                delay(500)
                val request = chatGptChannel.receive()
                request.response.send(Response("TestAgent response: ${request.message}"))
            }
        }

        // Wait for AgentService to process the input and save memory
        await().atMost(Duration.ofSeconds(10))
                .pollDelay(Duration.ofMillis(1000))
                .untilAsserted {
                    val memoryRecords = mementoRepository.findAll()
                    logger.info(objectMapper.writeValueAsString(memoryRecords))
                    assertThat(memoryRecords).isNotEmpty
                    assertThat(memoryRecords).anyMatch {
                        it.content == "Hello TestAgent"
                    }
                    assertThat(memoryRecords).anyMatch {
                        it.content == "TestAgent response: Hello TestAgent"
                    }
                }
    }
}
