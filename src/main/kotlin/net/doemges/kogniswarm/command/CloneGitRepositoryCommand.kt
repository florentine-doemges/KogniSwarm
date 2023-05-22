package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class CloneGitRepositoryCommand : BaseCommand(
    name = "cloneGitRepository",
    description = "Clones a Git repository from a URL to a specified path. Useful for project setup.",
    args = mapOf(
        "url" to "Repository URL.",
        "path" to "Target path."
    )

) {
    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}