package net.doemges.kogniswarm.extraction.service

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.extraction.util.SeleniumExtractor
import net.doemges.kogniswarm.extraction.util.SimpleExtractor
import net.doemges.kogniswarm.extraction.util.ContentExtractor
import net.doemges.kogniswarm.extraction.model.Extract
import net.doemges.kogniswarm.extraction.model.ExtractionContentType
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

    private fun extractSimple(
        url: String,
        contentType: ExtractionContentType = ExtractionContentType.TEXT
    ): Flow<Extract> = SimpleExtractor(
        webClientBuilder.baseUrl(url)
            .build()
    ).extract(url, ContentExtractor(contentType))

    private fun extractWithSelenium(url: String, contentType: ExtractionContentType): Flow<Extract> =
        SeleniumExtractor(
            browserService.getWebDriver()
        ).extract(url, ContentExtractor(contentType))
}

