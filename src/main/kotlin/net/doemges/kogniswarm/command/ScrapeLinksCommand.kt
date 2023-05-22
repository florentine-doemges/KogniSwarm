package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class ScrapeLinksCommand : BaseCommand(
    name = "scrapeLinks",
    description = "Extracts and returns all links from a website. Useful for website crawling.",
    args = mapOf("url" to "Website URL.")
) {
    override fun execute(args: Map<String, List<String>>): CommandOutput {
        TODO("Not yet implemented")
    }
}