package net.doemges.kogniswarm.chat.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = ChatMessageDeserializer::class)
class ChatMessage(val role: Role, content: String? = null) :
    Map<String, String> by mapOf("role" to role.toString(), "content" to (content ?: "")) {
    val content: String = content ?: ""
}


class ChatMessageDeserializer : JsonDeserializer<ChatMessage>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChatMessage {
        val node: JsonNode = p.codec.readTree(p)
        val roleText = node.get("role").asText()
        val role = java.lang.Enum.valueOf(Role::class.java, roleText.toUpperCase())
        val content = node.get("content").asText()
        return ChatMessage(role, content)
    }
}