package net.doemges.kogniswarm.chat

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotNull
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.awaitility.Awaitility.await
import org.jbehave.core.annotations.Given
import org.jbehave.core.annotations.Then
import org.jbehave.core.annotations.When
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.TimeUnit

@SpringBootTest
@ActiveProfiles("test")
class CustomizeParametersTest {

    @Autowired
    private lateinit var chatService: ChatService

    private lateinit var input: ChatMessageBundle
    private lateinit var result: String
    private var temperature: Double? = null

    @Given("a text prompt \"\$prompt\"")
    fun givenATextPrompt(prompt: String) {
        input = ChatMessageBundle.fromInput(prompt)
    }

    @Given("the temperature is set to \$temperature")
    fun givenTemperatureIsSetTo(temperature: Double) {
        this.temperature = temperature
    }

    @When("the user generates text with GPT-4")
    fun whenUserGeneratesTextWithGPT4() {
        runBlocking {
            result = chatService.sendToChatGpt(input)
        }
    }

    @Then("the output should be a story with a creativity level reflecting the temperature setting")
    fun thenTheOutputShouldBeAStoryWithCreativityLevel() {
        await().atMost(10, TimeUnit.SECONDS).untilAsserted {
            assertThat(result).isNotNull()

            val creativityInput = ChatMessageBundle.fromInput(
                "On a scale of 1 to 10, how creative is the following text based on a temperature setting of $temperature? Text: \"$result\"",
                "answer with only the number, parseable as a Double in Kotlin"
            )
            val creativityScore = runBlocking {
                chatService.sendToChatGpt(creativityInput)
            }
            val score = creativityScore.toDoubleOrNull()

            assertThat(score).isNotNull()
            assertThat(score!!).isGreaterThanOrEqualTo(1.0)
            assertThat(score).isEqualTo(temperature!! * 10)
        }
    }

    @Test
    fun `Customize GPT-4 parameters`() {
        givenATextPrompt("Once upon a time")
        givenTemperatureIsSetTo(0.8)
        whenUserGeneratesTextWithGPT4()
        thenTheOutputShouldBeAStoryWithCreativityLevel()
    }
}
