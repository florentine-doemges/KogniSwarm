package net.doemges.kogniswarm.extraction

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import net.doemges.kogniswarm.extraction.util.PooledWebDriver
import net.doemges.kogniswarm.extraction.model.Extract
import net.doemges.kogniswarm.extraction.model.ExtractionContentType
import net.doemges.kogniswarm.extraction.service.BrowserService
import net.doemges.kogniswarm.extraction.service.ExtractorService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebElement
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@OptIn(ExperimentalCoroutinesApi::class)
class ExtractorServiceTest {

    private lateinit var webClientBuilder: WebClient.Builder
    private lateinit var webClient: WebClient
    private lateinit var browserService: BrowserService
    private lateinit var extractorService: ExtractorService

    @BeforeEach
    fun setUp() {
        webClientBuilder = mockk<WebClient.Builder>()
        webClient = mockk()
        browserService = mockk()

        every { webClientBuilder.baseUrl(any()) } returns webClientBuilder
        every { webClientBuilder.build() } returns webClient

        extractorService = ExtractorService(webClientBuilder, browserService)
    }

    @Test
    fun `should return extracts from a simple extraction`() = runBlockingTest {
        val url = "http://test.com"
        val contentType = ExtractionContentType.TEXT
        val expectedExtract = Extract("test")

        every {
            webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String::class.java)
        } returns Mono.just("test")

        val result = runBlocking {
            extractorService.extract(url, contentType, false)
                .toList()
        }

        assertThat(result).containsExactly(expectedExtract)
        verify(exactly = 1) {
            webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String::class.java)
        }
    }

    @Test
    fun `should return extracts from a selenium extraction`() = runBlockingTest {
        val url = "http://test.com"
        val contentType = ExtractionContentType.TEXT
        val expectedExtract = Extract("test")
        val webDriver = mockk<PooledWebDriver>()
        every { webDriver.get(any()) } just Runs
        every { webDriver.pageSource } returns "test"
        every { webDriver.executeScript(any<String>(), *anyVararg()) } returns mockk()
        every { webDriver.findElement(any()) } returns mockk<WebElement>().apply {
            every { text } returns "test"
        }
        coEvery { browserService.getWebDriver() } returns webDriver

        val result = runBlocking {
            extractorService.extract(url, contentType, true)
                .toList()
        }

        assertThat(result).containsExactly(expectedExtract)
        coVerify(exactly = 1) { browserService.getWebDriver() }
    }
}
