package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class ExecuteCodeCommand : BaseCommand(
    name = "executeCode",
    description = "Executes the provided code snippet. Useful for dynamic code execution.",
    args = mapOf("code" to "File name to execute.")
) {
    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}