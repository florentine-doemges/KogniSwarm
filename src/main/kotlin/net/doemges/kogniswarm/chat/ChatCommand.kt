package net.doemges.kogniswarm.chat

import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class ChatCommand(private val chatService: ChatService) {
    @ShellMethod
    fun chat(message: String): String = chatService.sendToChatGpt(ChatMessageBundle.fromInput(message))
}