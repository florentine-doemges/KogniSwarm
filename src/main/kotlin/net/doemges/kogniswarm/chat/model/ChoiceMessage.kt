package net.doemges.kogniswarm.chat.model

/**
 * Message represents a single message in the completion.
 *
 * @property role The role of the message (e.g., "assistant" or "user").
 * @property content The content of the message.
 */
data class ChoiceMessage(
    val role: String,
    val content: String
)