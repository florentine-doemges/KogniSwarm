package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class ReadFromFileCommand : BaseCommand(
    name = "readFromFile",
    description = "Reads and returns the content of a specified file. Useful for data retrieval.",
    args = mapOf("filename" to "File to read.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}