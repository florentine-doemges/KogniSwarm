package net.doemges.kogniswarm.summary

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotEqualTo
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit

@SpringBootTest
class SummaryServiceTest {

    @Autowired
    private lateinit var chatService: ChatService

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Test
    fun `test summarizeText`() {
        val summaryService = SummaryService(chatService, restTemplate)

        val testText = "This is a test text for summary. The main goal is to ensure the functionality of the summarizer."
        val maxChunkSize = 2048
        val summaryMode = SummaryMode.WEIGHTED

        val summary = summaryService.summarizeText(testText, maxChunkSize, summaryMode)

        // Verify that the summary is not empty
        assertThat(summary).isNotEmpty()

        // Verify that the summary is different from the input
        assertThat { summary }.isNotEqualTo(testText)

        // Verify that the output length is less than or equal to maxChunkSize
        assert(summary.length <= maxChunkSize)

        // Verify the content of the summary (use awaitility for async operations)
        await().atMost(10, TimeUnit.SECONDS).untilAsserted {
            assertThat { chatService.sendToChatGpt(ChatMessageBundle.fromInput("$summaryMode: $testText")) }.isNotEqualTo(testText)
        }
    }
}
