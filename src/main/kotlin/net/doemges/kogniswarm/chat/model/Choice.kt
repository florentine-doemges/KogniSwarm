package net.doemges.kogniswarm.chat.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Choice represents a single generated choice for the input message.
 *
 * @property message The generated message object.
 * @property finish_reason The reason for the completion to finish (e.g., "stop", "length", or "temperature").
 * @property index The index of the choice in the list of choices.
 */
data class Choice(
    @JsonProperty("message") val message: ChatMessage,
    @JsonProperty("finish_reason") val finishReason: String,
    @JsonProperty("index") val index: Int
)
