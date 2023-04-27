package net.doemges.kogniswarm.http

import net.doemges.kogniswarm.summary.SummaryService
import org.springframework.stereotype.Service

@Service
class UrlSummaryService(
    private val summaryService: SummaryService,
    private val seleniumRestTemplate: SeleniumRestTemplate
) {
    fun summarizeUrl(url: String, maxChunkSize: Int = 2048): String {
        val pageContent = seleniumRestTemplate.getForObject(url, String::class.java)
        return pageContent?.let { content ->
            summaryService.summarizeText(content, maxChunkSize)
        } ?: ""
    }
}
