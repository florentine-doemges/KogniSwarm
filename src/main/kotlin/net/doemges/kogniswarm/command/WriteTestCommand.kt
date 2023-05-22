package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class WriteTestCommand : BaseCommand(
    name = "writeTest",
    description = "Generates testable Kotlin code for the provided code. Useful for code testing automation.",
    args = mapOf("code" to "Code to test.")
) {
    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}