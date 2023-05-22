package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class WriteToFileCommand : BaseCommand(
    name = "writeToFile",
    description = "Writes the provided text to a specified file. Useful for data archiving.",
    args = mapOf(
        "text" to "Text to write.",
        "filename" to "Target file."
    )

) {
    override fun execute(args: Map<String, List<String>>): CommandOutput {
        TODO("Not yet implemented")
    }
}