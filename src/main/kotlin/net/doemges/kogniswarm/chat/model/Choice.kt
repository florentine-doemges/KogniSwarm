package net.doemges.kogniswarm.chat.model

/**
 * Choice represents a single generated choice for the input message.
 *
 * @property message The generated message object.
 * @property finish_reason The reason for the completion to finish (e.g., "stop", "length", or "temperature").
 * @property index The index of the choice in the list of choices.
 */
data class Choice(
    val message: ChoiceMessage,
    val finish_reason: String,
    val index: Int
) {
}