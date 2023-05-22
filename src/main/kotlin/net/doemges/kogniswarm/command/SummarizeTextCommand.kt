package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class SummarizeTextCommand : BaseCommand(
    name = "summarizeText",
    description = "Creates a summary of the provided text. Useful for condensing lengthy text.",
    args = mapOf("text" to "Text to summarize.")
) {
    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}