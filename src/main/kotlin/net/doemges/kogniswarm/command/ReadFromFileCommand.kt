package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class ReadFromFileCommand : BaseCommand(
    name = "readFromFile",
    description = "Reads and returns the content of a specified file. Useful for data retrieval.",
    args = mapOf("filename" to "File to read.")
) {
    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}