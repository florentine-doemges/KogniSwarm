package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class ScrapeLinksCommand : BaseCommand(
    name = "scrapeLinks",
    description = "Extracts and returns all links from a website. Useful for website crawling.",
    args = mapOf("url" to "Website URL.")
) {
    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}