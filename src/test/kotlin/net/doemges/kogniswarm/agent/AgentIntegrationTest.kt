package net.doemges.kogniswarm.agent

import assertk.assertThat
import assertk.assertions.isNotEmpty
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.data.Fixtures
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class AgentIntegrationTest(@Autowired private val kord: Kord) {

    private val fixture = Fixtures.fixtureWithFaker()

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `Agent should add message to messageQueue when message is received`() = runBlocking {
        val testChannelId = 1102449789146247168
        val agentScope = CoroutineScope(
            Executors.newSingleThreadExecutor()
                    .asCoroutineDispatcher()
        )
        val agent = Agent(agentScope, fixture(), kord)

        // Assuming you have a test channel set up in your Discord server
        val testChannel = kord.getChannelOf<TextChannel>(Snowflake(testChannelId))!!

        // Send a message to the test channel
        testChannel.createMessage("@${agent.id.name} Test message from AgentIntegrationTest")

        delay(2000)

        // Await for the message to be processed
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted {
                    assertThat(agent.messageQueue).isNotEmpty()
                }
    }
}
