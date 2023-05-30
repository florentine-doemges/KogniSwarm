package net.doemges.kogniswarm.tool.browse

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.toCollection
import net.doemges.kogniswarm.action.Action
import net.doemges.kogniswarm.core.ParameterParser
import net.doemges.kogniswarm.extraction.Extract
import net.doemges.kogniswarm.extraction.ExtractionContentType
import net.doemges.kogniswarm.extraction.ExtractorService
import net.doemges.kogniswarm.tool.BaseTool
import org.apache.camel.Exchange
import org.springframework.stereotype.Component

@Component
class BrowseWebTool(
    parameterParser: ParameterParser,
    private val extractorService: ExtractorService,
    private val objectMapper: ObjectMapper
) : BaseTool(parameterParser) {
    override suspend fun processWithParams(
        parsedParams: Map<String, String>,
        toolUri: String,
        toolParams: String,
        exchange: Exchange
    ) {
        val url = parsedParams["url"] ?: error("URL is not specified")
        val contentType = ExtractionContentType.ofString(parsedParams["contentType"] ?: "text")!!
        val selenium = parsedParams["selenium"]?.toBoolean() ?: false

        val extracts: List<Extract> = extractorService.extract(url, contentType, selenium).toCollection(mutableListOf())

        val action = Action(
            tool = this,
            args = mapOf(
                "url" to url,
                "contentType" to contentType.toString(),
                "selenium" to selenium.toString()
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