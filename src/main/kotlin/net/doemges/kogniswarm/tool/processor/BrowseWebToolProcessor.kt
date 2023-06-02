package net.doemges.kogniswarm.tool.processor

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.toCollection
import net.doemges.kogniswarm.action.model.Action
import net.doemges.kogniswarm.core.util.ParameterParser
import net.doemges.kogniswarm.extraction.model.Extract
import net.doemges.kogniswarm.extraction.model.ExtractionContentType
import net.doemges.kogniswarm.extraction.service.ExtractorService
import org.apache.camel.Exchange
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class BrowseWebToolProcessor(
    parameterParser: ParameterParser,
    private val extractorService: ExtractorService,
    private val objectMapper: ObjectMapper
) : BaseToolProcessor(parameterParser) {
    override suspend fun processWithParams(
        parsedParams: Map<String, String>,
        toolUri: String,
        toolParams: String,
        exchange: Exchange
    ) {
        val url = parsedParams["url"] ?: error("URL is not specified")
        val contentType = ExtractionContentType.ofString(parsedParams["contentType"] ?: "text")!!
        val selenium = parsedParams["selenium"]?.toBoolean() ?: false

        val extracts: List<Extract> = try {
            extractorService.extract(url, contentType, selenium).toCollection(mutableListOf())
        } catch (e: WebClientResponseException) {
            when{
                e.statusCode.is4xxClientError -> emptyList()
                else -> throw e
            }
        }

        val action = Action(
            toolProcessor = this,
            args = mapOf(
                "url" to url,
                "contentType" to contentType.toString()
            ),
            result = objectMapper.writeValueAsString(extracts),
        )
        exchange.message.headers["action"] = action
    }

    override val name: String = "browseWeb"
    override val description: String = "Browse the web"
    override val args: Map<String, String> = mapOf(
        "url" to "URL to browse (required)",
        "contentType" to "Type of content to retrieve (default: text). Possible values: text, images, links",
        "selenium" to "Whether to use Selenium for browsing (default: false). Possible values: true, false"
    )
    override val keys: List<String> = listOf("url", "contentType", "selenium")

}