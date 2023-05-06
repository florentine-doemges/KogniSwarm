package net.doemges.kogniswarm.http

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isNotEmpty
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.TimeUnit

@SpringBootTest
class UrlSummaryServiceTest {

    @Autowired
    lateinit var urlSummaryService: UrlSummaryService

    @Autowired
    lateinit var chatService: ChatService

    @Test
    fun `should summarize example dot com correctly`() {
        val exampleUrl = "https://www.example.com"

        await().atMost(120, TimeUnit.SECONDS).untilAsserted {
            val summary = urlSummaryService.summarizeUrl(exampleUrl)
            assertThat(summary).isNotEmpty()

            val chatMessageBundle = ChatMessageBundle.fromInput("Is this a good summary of $exampleUrl? $summary", "answer just with YES or NO. No other answer is accepted.")
            val evaluation = chatService.sendToChatGpt(chatMessageBundle)

            assertThat(evaluation).contains("YES")
        }
    }
}
