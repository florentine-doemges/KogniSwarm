package net.doemges.kogniswarm.prompt

import net.doemges.kogniswarm.command.Command
import org.springframework.stereotype.Service

@Service
class PromptService(private val commands: List<Command>) {
    fun createPromptFromText(text: String): Prompt = Prompt.fromUserRequest(text, commands)

}