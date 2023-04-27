package net.doemges.kogniswarm.chat

import assertk.all
import assertk.assertThat
import assertk.assertions.*
import net.doemges.kogniswarm.chat.model.ChatMessage
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import net.doemges.kogniswarm.chat.model.Role
import org.awaitility.Awaitility.await
import org.jbehave.core.annotations.Given
import org.jbehave.core.annotations.Then
import org.jbehave.core.annotations.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.extension.TestWatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit

@SpringBootTest
class GenerateSimpleTextTest {

    @Autowired
    private lateinit var chatService: ChatService

    lateinit var input: ChatMessageBundle
    lateinit var generatedText: String

    @Given("a text prompt \"tell me a joke\"")
    fun givenTextPrompt() {
        input = ChatMessageBundle(ChatMessage(Role.USER, "tell me a joke"))
    }

    @When("the user generates text with GPT-4")
    fun whenGenerateTextWithGpt4() {
        generatedText = chatService.sendToChatGpt(input)
    }

    @Then("the output should be a coherent and relevant joke")
    fun thenOutputShouldBeCoherentAndRelevantJoke() {
        assertThat { generatedText }
            .isSuccess()
            .all {
                isNotNull()
                isNotEmpty()
            }
        assertThat { generatedText.split(" ") }.isSuccess().size().isGreaterThanOrEqualTo(5)
        assertThat { chatService.sendToChatGpt(ChatMessageBundle.fromInput("Is '$generatedText' a a coherent and relevant joke? Please answer yes or no.")) }
            .isSuccess()
            .contains("yes", true)
    }

    companion object {
        @JvmField
        @RegisterExtension
        val testListener = object : TestWatcher {
            override fun testFailed(context: ExtensionContext?, cause: Throwable?) {
                println("Test ${context?.displayName} failed with ${cause?.message}")
            }

            override fun testSuccessful(context: ExtensionContext?) {
                println("Test ${context?.displayName} succeeded")
            }
        }
    }

    @Test
    fun `Generate simple text`() {
        givenTextPrompt()
        whenGenerateTextWithGpt4()
        thenOutputShouldBeCoherentAndRelevantJoke()
    }
}
