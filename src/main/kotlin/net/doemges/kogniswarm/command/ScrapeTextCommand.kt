package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class ScrapeTextCommand : BaseCommand(
    name = "scrapeText",
    description = "Extracts and returns the text content of a website. Useful for web content extraction.",
    args = mapOf("url" to "Website URL.")
) {
    override fun execute(args: Map<String, List<String>>): CommandOutput {
        TODO("Not yet implemented")
    }
}