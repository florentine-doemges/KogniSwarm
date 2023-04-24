package net.doemges.kogniswarm.chat.model

class ChatMessage(val role: Role, content: String? = null) :
    Map<String, String> by mapOf("role" to role.toString(), "content" to (content ?: "")) {
    val content: String = content ?: ""
}


