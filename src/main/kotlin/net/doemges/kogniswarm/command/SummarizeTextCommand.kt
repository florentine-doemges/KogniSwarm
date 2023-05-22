package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class SummarizeTextCommand : BaseCommand(
    name = "summarizeText",
    description = "Creates a summary of the provided text. Useful for condensing lengthy text.",
    args = mapOf("text" to "Text to summarize.")
) {
    override fun execute(args: Map<String, List<String>>): CommandOutput {
        TODO("Not yet implemented")
    }
}