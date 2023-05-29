package net.doemges.kogniswarm.extraction

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.containers.BrowserService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class ExtractorService(
    private val webClientBuilder: WebClient.Builder,
    private val browserService: BrowserService
) {
    suspend fun extract(url: String, contentType: ExtractionContentType, selenium: Boolean): Flow<Extract> {
        if (selenium) {
            return extractWithSelenium(url, contentType)
        }
        return extractSimple(url, contentType)
    }

    private fun createContentExtractor(contentType: ExtractionContentType): ContentExtractor = when (contentType) {
        ExtractionContentType.TEXT -> TextContentExtractor()
        ExtractionContentType.IMAGES -> ImageContentExtractor()
        ExtractionContentType.LINKS -> LinkContentExtractor()
    }

    private fun extractSimple(
        url: String,
        contentType: ExtractionContentType = ExtractionContentType.TEXT
    ): Flow<Extract> = SimpleExtractor(
        webClientBuilder.baseUrl(url)
            .build()
    ).extract(url, createContentExtractor(contentType))

    private fun extractWithSelenium(url: String, contentType: ExtractionContentType): Flow<Extract> =
        SeleniumExtractor(
            browserService.getWebDriver()
        ).extract(url, createContentExtractor(contentType))
}

