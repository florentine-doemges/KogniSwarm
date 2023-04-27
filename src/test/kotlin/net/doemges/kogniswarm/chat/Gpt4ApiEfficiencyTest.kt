package net.doemges.kogniswarm.chat

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.jbehave.core.annotations.Given
import org.jbehave.core.annotations.Then
import org.jbehave.core.annotations.When
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration

@ExtendWith(SpringExtension::class)
@SpringBootTest
class ChatServiceEfficiencyTest {

    var apiCalls = 0
    var totalResponseTime: Duration = Duration.ZERO
    private val acceptableApiCalls = 5
    private val acceptableResponseTime = Duration.ofSeconds(30)

    @Autowired
    private lateinit var chatService: ChatService

    private var prompt: String? = null

    @When("the user makes \$acceptableApiCalls API calls")
    fun whenUserMakesApiCalls() {


        repeat(acceptableApiCalls) {
            val startTime = System.currentTimeMillis()
            chatService.sendToChatGpt(ChatMessageBundle.fromInput(prompt!!))
            val endTime = System.currentTimeMillis()

            totalResponseTime += Duration.ofMillis(endTime - startTime)
            apiCalls++
        }


    }

    @Test
    fun `Ensure API efficiency`() {
        givenTextPrompt()
        whenUserMakesApiCalls()
        thenOutputShouldMeetEfficiencyRequirements()
    }

    private fun thenOutputShouldMeetEfficiencyRequirements() {
        assertThat(apiCalls).isEqualTo(acceptableApiCalls)
        assertThat(totalResponseTime).isLessThanOrEqualTo(acceptableResponseTime)
    }

    @Given("a text prompt \"tell me a fact about space\"")
    private fun givenTextPrompt() {
        prompt = "tell me a fact about space"
    }
}
